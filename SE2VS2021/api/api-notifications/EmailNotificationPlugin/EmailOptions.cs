using Microsoft.Extensions.Configuration;

namespace EmailNotificationPlugin;

public class EmailOptions
{
    public string? MailFrom { get; }
    public string? Password { get; }

    public EmailOptions(IConfigurationSection pluginOptions)
    {
        MailFrom = pluginOptions["MailFrom"];
        Password = pluginOptions["Password"];
    }
}