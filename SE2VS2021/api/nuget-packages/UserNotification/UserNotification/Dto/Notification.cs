namespace UserNotification.Dto;

public class Notification
{
    public Guid UserId;
    public string Type;
    public string Message;

    public Notification(Guid userId, string type, string message)
    {
        UserId = userId;
        Type = type;
        Message = message;
    }
}