using System.Text;
using api_version_logging.Config;
using api_version_logging.Dto;
using Microsoft.Extensions.Options;
using Newtonsoft.Json;
using RabbitMQ.Client;
using RabbitMQ.Client.Events;

namespace api_version_logging.Services;

public class LogBackgroundService: BackgroundService
{
    private readonly VersionLogSettings _versionLogSettings;
    private IServiceProvider Services { get; }
    private readonly ILogger<LogBackgroundService> _logger;
    private readonly IConnection _connection;  
    private readonly IModel _channel;

    public LogBackgroundService(IOptions<VersionLogSettings> rabbitSettings, IServiceProvider services, ILogger<LogBackgroundService> logger)
    {
        _versionLogSettings = rabbitSettings.Value;
        Services = services;
        _logger = logger;
        var factory = new ConnectionFactory { HostName = _versionLogSettings.HostName };  
  
        // create connection  
        _connection = factory.CreateConnection();  
  
        // create channel  
        _channel = _connection.CreateModel();  
  
        //_channel.ExchangeDeclare("demo.exchange", ExchangeType.Topic);  
        _channel.QueueDeclare(_versionLogSettings.LogQueueName, false, false, false, null);  
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
  
        _channel.BasicConsume(_versionLogSettings.LogQueueName, false, consumer);  
        return Task.CompletedTask;  
    }  
  
    private async void HandleMessage(string body, CancellationToken stoppingToken)  
    {  
        var logDto = JsonConvert.DeserializeObject<NewLogDto>(body);
        if (logDto == null)
        {
            _logger.LogError("Failed to deserialize Log Object");
        }
        else
        {
            using var scope = Services.CreateScope();
            var logService = 
                scope.ServiceProvider
                    .GetRequiredService<ILogService>();
            await logService.AddNewLogEntry(logDto);
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