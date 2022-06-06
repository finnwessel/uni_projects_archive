using System.ComponentModel.DataAnnotations;
using System.Text.Json.Nodes;

namespace api_notifications.Models;

public class NotificationSettings
{
    [Key] public virtual Guid Id { get; set; }
    public string? Active { get; set; } = null;
    public string Options { get; set; } = "{}";
}