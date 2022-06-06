namespace api_tasks.Exceptions;

public class TaskNotFoundException : Exception
{
    public TaskNotFoundException () {}
    public TaskNotFoundException (string message) : base(message){}
    
}