using Microsoft.Extensions.Configuration;

namespace NotificationPluginBase;

public interface INotificationPlugin
{
    string Type { get; }
    string Name { get; }
    string Description { get; }
    int Setup(IConfigurationSection pluginConf);
    int SendNotification(Dictionary<string, string?> userSettings, string subject, string message);
    Dictionary<string, string?> UserSpecificConfig();
}