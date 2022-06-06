using api_notifications.Context;
using api_notifications.Models;
using Newtonsoft.Json;

namespace api_notifications.services;

public interface INotificationService
{
    bool PluginExists(string type);
    Task<bool> SendNotificationWithSelectedPlugin(Guid userId, string subject, string message);
    Task<Dictionary<string, string?>?> GetUserNotificationPluginSettings(Guid id, string type);
    Task<bool> ConfigureNotificationPlugin(Guid id, string type, Dictionary<string, string?> config);
    Task<bool> ActivateNotificationPlugin(Guid id, string pluginName);
    Task<string?> GetActiveNotificationType(Guid userId);
    Task<bool> DisableNotifications(Guid userId);
    List<string> NotificationTypes();
}

public class NotificationService : INotificationService
{
    private readonly MariaDbContext _context;
    private readonly INotificationPluginService _pluginService;

    public NotificationService(MariaDbContext context, INotificationPluginService pluginService)
    {
        _context = context;
        _pluginService = pluginService;
    }

    public bool PluginExists(string type)
    {
        return _pluginService.PluginExists(type);
    }

    public async Task<bool> SendNotificationWithSelectedPlugin(Guid userId, string subject, string message)
    {
        var conf = await GetUserNotificationSettings(userId);
        if (conf.Active == null)
        {
            return false;
        }

        if (!PluginExists(conf.Active))
        {
            return false;
        }

        var plugin = _pluginService.GetPlugin(conf.Active);
        if (plugin == null)
        {
            return false;
        }

        var userConf = JsonConvert.DeserializeObject<Dictionary<string, Dictionary<string, string?>>>(conf.Options);
        if (userConf == null)
        {
            return false;
        }

        userConf.TryGetValue(conf.Active, out var settings);
        if (settings == null)
        {
            return false;
        }
        
        return (plugin.SendNotification(settings,  subject, message) == 1);
    }
    public async Task<Dictionary<string, string?>?> GetUserNotificationPluginSettings(Guid id, string type)
    {
        var conf = await GetUserNotificationSettings(id);
        var options = JsonConvert.DeserializeObject<Dictionary<string, Dictionary<string, string?>>>(conf.Options);
        if (options != null && options.TryGetValue(type, out var pluginSettings))
        {
            return pluginSettings;
        }

        var plugin = _pluginService.GetPlugin(type);
        return plugin?.UserSpecificConfig();
    }

    public async Task<bool> ConfigureNotificationPlugin(Guid id, string type, Dictionary<string, string?> config)
    {
        // check if config exists
        var settings = await _context.NotificationSettings.FindAsync(id);
        var existed = true;
        if (settings == null)
        {
            settings = new NotificationSettings
            {
                Id = id
            };
            existed = false;
        }

        var options = JsonConvert.DeserializeObject<Dictionary<string, Dictionary<string, string?>?>>(settings.Options);

        if (options == null) return false;
        options[type] = config;

        settings.Options = JsonConvert.SerializeObject(options);
        if (existed)
        {
            _context.NotificationSettings.Update(settings);    
        }
        else
        {
            _context.NotificationSettings.Add(settings);
        }
        
        return (await _context.SaveChangesAsync() > 0);
    }

    public async Task<bool> ActivateNotificationPlugin(Guid userId, string pluginName)
    {
        if (!_pluginService.PluginExists(pluginName)) return false;
        var settings = await GetUserNotificationSettings(userId);
        settings.Active = pluginName;
        var result = await _context.SaveChangesAsync();
        return result > 0;
    }

    public async Task<string?> GetActiveNotificationType(Guid userId)
    {
        var settings = await GetUserNotificationSettings(userId);
        return settings.Active;
    }

    public async Task<bool> DisableNotifications(Guid userId)
    {
        var settings = await GetUserNotificationSettings(userId);
        settings.Active = null;
        var result = await _context.SaveChangesAsync();
        return result > 0;
    }

    public List<string> NotificationTypes()
    {
        return _pluginService.GetPluginTypes();
    }

    private async Task<NotificationSettings> GetUserNotificationSettings(Guid userId)
    {
        var settings = await _context.NotificationSettings.FindAsync(userId);
        if (settings == null)
        {
            var obj = JsonConvert.SerializeObject(new Dictionary<string, Dictionary<string, string?>>());
            settings = new NotificationSettings
            {
                Id = userId,
                Options = obj
            };
            _context.NotificationSettings.Add(settings);
            await _context.SaveChangesAsync();
        }

        return settings;
    }
}