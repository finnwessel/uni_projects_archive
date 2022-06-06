using System.IdentityModel.Tokens.Jwt;
using System.Text.Json.Nodes;
using api_notifications.services;
using JwtMiddleware.Helpers;
using JwtMiddleware.Services;
using Microsoft.AspNetCore.Mvc;
using Newtonsoft.Json;
using UserNotification.Dto;

namespace api_notifications.Controllers;

[ApiController]
[Route("[controller]")]
public class NotificationsController: ControllerBase
{
    private readonly IAuthService _authService;
    private readonly IMessageQueue _messageQueue;
    private readonly INotificationService _notificationService;

    public NotificationsController(IAuthService authService, IMessageQueue messageQueue, INotificationService notificationService)
    {
        _authService = authService;
        _messageQueue = messageQueue;
        _notificationService = notificationService;
    }
    
    [HttpGet]
    [Route("messages/subscribe")]
    public async Task Subscribe([FromQuery] string token)
    {
        var securityToken = (JwtSecurityToken?) _authService.ValidateToken(token);
        
        if (securityToken == null)
        {
            Response.StatusCode = 401;
            return;
        }

        var userId = Guid.Parse(securityToken.Claims.First(x => x.Type == "id").Value);

        Response.ContentType = "text/event-stream";
        Response.StatusCode = 200;

        var streamWriter = new StreamWriter(Response.Body);

        _messageQueue.Register(userId);
        //var heartBeatToken = new CancellationTokenSource();
        try
        {
            //SendHeartbeat(streamWriter, heartBeatToken.Token);
            await _messageQueue.EnqueueAsync(userId, new Notification(userId, "connected", "Started to listen for notifications"), HttpContext.RequestAborted);

            await foreach (var message in _messageQueue.DequeueAsync(userId, HttpContext.RequestAborted))
            {
                var data = JsonConvert.SerializeObject(new
                {
                    time = DateTime.Now,
                    message = message.Message
                });
                await streamWriter.WriteLineAsync($"event: {message.Type}\ndata: {data}\n");
                await streamWriter.FlushAsync();
            }
        }
        catch(OperationCanceledException)
        {
            //heartBeatToken.Cancel();
        }
        catch(Exception)
        {
            Response.StatusCode = 400;
            //heartBeatToken.Cancel();
        }
        finally
        {
            //heartBeatToken.Cancel();
            _messageQueue.Unregister(userId);
        }
    }
    
    [HttpGet]
    [Route("sse")]
    public async Task SimpleSse()
    {
        //1. Set content type
        Response.ContentType = "text/event-stream";
        Response.StatusCode = 200;

        StreamWriter streamWriter = new StreamWriter(Response.Body);

        while(!HttpContext.RequestAborted.IsCancellationRequested)
        {
            //2. Await something that generates messages
            await Task.Delay(5000, HttpContext.RequestAborted);
			
            //3. Write to the Response.Body stream
            await streamWriter.WriteLineAsync($"{DateTime.Now} Looping");
            await streamWriter.FlushAsync();
			
        }
    }
    
    [Auth]
    [HttpPost]
    [Route("activate/{type}")]
    public async Task<IActionResult> ActivateNotificationType(string type)
    {
        var userId = (Guid?) HttpContext.Items["AuthUserId"];

        if (userId == null)
        {
            return BadRequest();
        }
        
        var activated = await _notificationService.ActivateNotificationPlugin(userId.Value, type);
        if (activated)
        {
            return Ok();
        }

        return BadRequest();
    }
    
    [Auth]
    [HttpGet]
    [Route("active")]
    public async Task<IActionResult> GetActiveNotificationType()
    {
        var userId = (Guid?) HttpContext.Items["AuthUserId"];

        if (userId == null)
        {
            return BadRequest();
        }
        
        return Ok(await _notificationService.GetActiveNotificationType(userId.Value));
    }
    
    [Auth]
    [HttpPost]
    [Route("disable")]
    public async Task<IActionResult> DisableNotifications()
    {
        var userId = (Guid?) HttpContext.Items["AuthUserId"];

        if (userId == null)
        {
            return BadRequest();
        }

        var disabled = await _notificationService.DisableNotifications(userId.Value);
        if (disabled)
        {
            return Ok();
        }

        return BadRequest();
    }

    [Auth]
    [HttpGet]
    [Route("configuration/{type}")]
    public async Task<IActionResult> GetPluginConfiguration(string type)
    {
        var userId = (Guid?) HttpContext.Items["AuthUserId"];

        if (userId == null)
        {
            return BadRequest();
        }
        
        if (!_notificationService.PluginExists(type))
        {
            return NotFound();
        }

        var conf = await _notificationService.GetUserNotificationPluginSettings(userId.Value, type);

        if (conf == null)
        {
            return BadRequest();
        }

        return Ok(conf);

    }

    [Auth]
    [HttpPost]
    [Route("configure/{type}")]
    public async Task<IActionResult> ConfigurePlugin(string type, [FromBody] Dictionary<string, string?> conf)
    {
        var userId = (Guid?) HttpContext.Items["AuthUserId"];

        if (userId == null)
        {
            return BadRequest();
        }
        
        if (!_notificationService.PluginExists(type))
        {
            return NotFound();
        }

        if (await _notificationService.ConfigureNotificationPlugin(userId.Value, type, conf))
        {
            return Ok();   
        }

        return BadRequest();
    }
    

    [HttpGet]
    [Route("types")]
    public IActionResult NotificationTypes()
    {
        return Ok(_notificationService.NotificationTypes());
    }


    private async Task SendHeartbeat(StreamWriter streamWriter, CancellationToken cancellationToken)
    {
        await Task.Run(async () =>
        {
            while (true)
            {
                await streamWriter.WriteLineAsync($"event: heartbeat");
                await streamWriter.WriteLineAsync("data: { time: " + DateTime.Now + "}");
                await streamWriter.FlushAsync();
                await Task.Delay(10000, cancellationToken);
                if (cancellationToken.IsCancellationRequested)
                    break;
            }
        }, cancellationToken);
    }
}