# Description

This Package provides the Interface `INotificationPlugin`.
# Install

## Requirements

- .net6

## Nuget

Install latest `NotificationPluginBase` Package.

## Creating a plugin

Each plugin needs to implement the NotificationPluginBase Interface.
The `Type` of the plugin has to be unique. For example a plugin for email notifications could have the type `email`.
Only one plugin of a specific type can be loaded at once.

Each plugin retrieves a `Dictionary<string, string?>` which contains user specific configurations, required to send the notification to the specified user.
The plugin itself defines a Dictionary with required user settings.
```c#
public class SomeNotificationPlugin: INotificationPluginBase
{
    public string Type => "unique_type_of_plugin";
    public string Name => "name_of_plugin";
    public string Description => "description_of_plugin";

    // Implement other Methods from Interface
}
```