using System.Text;
using Microsoft.Extensions.Options;
using Newtonsoft.Json;
using RabbitMQ.Client;
using UserNotification.Dto;

namespace UserNotification;

public interface INotificationPublisher
{
    void Send(Guid userId, string type, string message);
}
public class NotificationPublisher: INotificationPublisher
{
    private readonly ConnectionFactory _factory;
    private readonly UserNotificationSettings _options;
    
    public NotificationPublisher(IOptions<UserNotificationSettings> options)
    {
        
        if (options == null)
        {
            throw new Exception("Missing RabbitMQ configuration");
        }
        _options = options.Value;
        _factory = new ConnectionFactory
        {
            HostName = _options.HostName
        };
    }

    public void Send(Guid userId, string type, string message)
    {
        using (var connection = _factory.CreateConnection())
        using (var channel = connection.CreateModel())
        {
            channel.QueueDeclare(queue: _options.QueueName,
                false,
                false,
                false,
                null);
            
            var notification = new Notification(userId, type, message);
            var body = Encoding.UTF8.GetBytes(JsonConvert.SerializeObject(notification));
            channel.BasicPublish(exchange: "",
                _options.QueueName,
                null, body);
        }
    }
}