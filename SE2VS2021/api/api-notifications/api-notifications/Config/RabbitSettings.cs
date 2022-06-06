namespace api_notifications.Config;

public class RabbitSettings
{ 
    public string HostName { get; set; } = null!;
    public string NotificationsQueueName { get; set; } = null!;
}