# Description

This Package provides the Class `VersionLogPublisher` with a `void Log()` method.

# Install

## Requirements

- .net6

## Nuget

Install latest `VersionLogging` Package.

## Settings

In `appsettings.json` and `appsettings.Development.json` add the following configuration.

```c#
"VersionLoggingSettings": {
    "HostName": "host_name_of_rabbitmq_instance",
    "QueueName": "queue_name_of_version_logging_queue"
}
```

## Program.cs

Get VersionLoggingSettings Configuration Settings
```c#
builder.Services.Configure<VersionLoggingSettings>(builder.Configuration.GetSection("VersionLoggingSettings"));
```

Register VersionLogPublisher
```c#
builder.Services.AddScoped<IVersionLogPublisher, VersionLogPublisher>();
```

## Controllers or Services

```c#
public class SomeControllerOrService
{
    private readonly IVersionLogPublisher _versionLogPublisher;
    
    public SomeControllerOrService(IVersionLogPublisher _versionLogPublisher)
    {
        _versionLogPublisher = versionLogPublisher
    }
    
    public void SomeFunctionPublishingVersionLog()
    {
        // referenzId is used to retrieve this log
        Guid referenceId = Guid.NewGuid();
        string message = "myVersionLogMessage";
        // system is used to filter for specific system
        string system = "systemNameSendingThisLog";
        // Optional, if log is user specific
        Guid userId = Guid.NewGuid();
        _versionLogPublisher.Log(referenceId, message, system, userId)
    }
}
```