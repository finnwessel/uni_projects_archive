using EmailNotificationPlugin.Services;
using Microsoft.Extensions.Configuration;
using NotificationPluginBase;

namespace EmailNotificationPlugin;

public class Plugin: INotificationPlugin
{
    private EmailOptions? _options = null;
    public string Type => "email";
    public string Name => "EmailNotificationPlugin";
    public string Description => "Sends Email Notifications to users";

    private SmtpService? _smtpService = null;
    public int Setup(IConfigurationSection pluginConf)
    {
        _options = new EmailOptions(pluginConf);
        if (_options.MailFrom == null || _options.Password == null) return -1;
        _smtpService = new SmtpService(_options.MailFrom, _options.Password);
        return 1;
    }

    public int SendNotification(Dictionary<string, string?> userSettings, string subject, string message)
    {
        if (_smtpService != null && userSettings.ContainsKey("email"))
        {
            //return _smtpService.SendMail(to, subject, "<html><head></head><body><b>Test HTML Email</b></body></html>");
            userSettings.TryGetValue("email", out var to);
            if (to == null)
            {
                return -1;
            }
            return _smtpService.SendMail(to, subject, message);
        }
        return -1;
    }

    public Dictionary<string, string?> UserSpecificConfig()
    {
        return new Dictionary<string, string?> {{"email", null}};
    }
}