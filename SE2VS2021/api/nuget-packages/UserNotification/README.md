# Description

This Package provides the Class `NotificationPublisher` with a `void Send()` method.

# Install

## Requirements

- .net6

## Nuget

Install latest `UserNotification` Package.

## Settings

In `appsettings.json` and `appsettings.Development.json` add the following configuration.

```c#
"UserNotificationSettings": {
    "HostName": "host_name_of_rabbitmq_instance",
    "QueueName": "queue_name_of_notification_queue"
},
```

## Program.cs

Get UserNotificationSettings Configuration Settings
```c#
builder.Services.Configure<UserNotificationSettings>(builder.Configuration.GetSection("UserNotificationSettings"));
```

Register NotificationPublisher
```c#
builder.Services.AddScoped<INotificationPublisher, NotificationPublisher>();
```

## Controllers or Services

```c#
public class SomeControllerOrService
{
    private readonly INotificationPublisher _notificationPublisher;
    
    public SomeControllerOrService(INotificationPublisher notificationPublisher)
    {
        _notificationPublisher = notificationPublisher
    }
    
    public void SomeFunctionPublishingUserMessage()
    {
        Guid userId = Guid.NewGuid();
        string type = "typeOfEvent";
        string message = "myRealtimeMessage";
        _notificationPublisher.Send(userId, type, message);
    }
}
```
