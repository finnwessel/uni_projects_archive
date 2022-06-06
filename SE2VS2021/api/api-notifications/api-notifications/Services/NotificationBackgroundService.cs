using System.Text;
using api_notifications.Config;
using Microsoft.Extensions.Options;
using Newtonsoft.Json;
using RabbitMQ.Client;
using RabbitMQ.Client.Events;
using UserNotification.Dto;

namespace api_notifications.services;

public class NotificationBackgroundService: BackgroundService
{
    private readonly RabbitSettings _rabbitSettings;
    private IServiceProvider Services { get; }
    private readonly ILogger<NotificationBackgroundService> _logger;
    private readonly IConnection _connection;  
    private readonly IModel _channel;

    public NotificationBackgroundService(IOptions<RabbitSettings> rabbitSettings, IServiceProvider services, ILogger<NotificationBackgroundService> logger)
    {
        _rabbitSettings = rabbitSettings.Value;
        Services = services;
        _logger = logger;
        var factory = new ConnectionFactory { HostName = _rabbitSettings.HostName };  
  
        // create connection  
        _connection = factory.CreateConnection();  
  
        // create channel  
        _channel = _connection.CreateModel();  
  
        //_channel.ExchangeDeclare("demo.exchange", ExchangeType.Topic);  
        _channel.QueueDeclare(_rabbitSettings.NotificationsQueueName, false, false, false, null);  
        //_channel.QueueBind("demo.queue.log", "demo.exchange", "demo.queue.*", null);  
        _channel.BasicQos(0, 1, false);  
  
        _connection.ConnectionShutdown += RabbitMQ_ConnectionShutdown; 
    }

    protected override Task ExecuteAsync(CancellationToken stoppingToken)  
    {  
        stoppingToken.ThrowIfCancellationRequested();  
  
        var consumer = new EventingBasicConsumer(_channel);  
        consumer.Received += (ch, ea) =>  
        {  
            // received message  
            var content = Encoding.UTF8.GetString(ea.Body.ToArray());  
  
            // handle the received message  
            HandleMessage(content, stoppingToken);  
            _channel.BasicAck(ea.DeliveryTag, false);  
        };  
  
        consumer.Shutdown += OnConsumerShutdown;  
        consumer.Registered += OnConsumerRegistered;  
        consumer.Unregistered += OnConsumerUnregistered;  
        consumer.ConsumerCancelled += OnConsumerConsumerCancelled;  
  
        _channel.BasicConsume(_rabbitSettings.NotificationsQueueName, false, consumer);  
        return Task.CompletedTask;  
    }  
  
    private async void HandleMessage(string body, CancellationToken stoppingToken)  
    {  
        var notification = JsonConvert.DeserializeObject<Notification>(body);
        if (notification == null)
        {
            _logger.LogError("Failed to deserialize Notification Object");
        }
        else
        {
            using (var scope = Services.CreateScope())
            {
                var messageQueue = 
                    scope.ServiceProvider
                        .GetRequiredService<IMessageQueue>();
                await messageQueue.EnqueueAsync(notification.UserId, notification, stoppingToken);

                if (!messageQueue.IsActiveSse(notification.UserId))
                {
                    var notificationService = scope.ServiceProvider.GetRequiredService<INotificationService>();
                    await notificationService.SendNotificationWithSelectedPlugin(notification.UserId,
                        "Gruppe 9 Benachrichtigung", notification.Message);   
                }
            }
            _logger.LogInformation($"notification for id: {notification?.UserId} and message: {notification?.Message}");   
        }
    }  
      
    private void OnConsumerConsumerCancelled(object? sender, ConsumerEventArgs e)  {  }  
    private void OnConsumerUnregistered(object? sender, ConsumerEventArgs e) {  }  
    private void OnConsumerRegistered(object? sender, ConsumerEventArgs e) {  }  
    private void OnConsumerShutdown(object? sender, ShutdownEventArgs e) {  }  
    private void RabbitMQ_ConnectionShutdown(object? sender, ShutdownEventArgs e)  {  }  
  
    public override void Dispose()  
    {  
        _channel.Close();  
        _connection.Close();  
        base.Dispose();  
    } 
}