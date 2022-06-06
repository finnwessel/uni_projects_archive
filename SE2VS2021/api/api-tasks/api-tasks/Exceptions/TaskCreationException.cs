namespace api_tasks.Exceptions;

public class TaskCreationException : Exception
{
    public TaskCreationException(){}

    public TaskCreationException(string message) : base(message)
    {
        
    }
}