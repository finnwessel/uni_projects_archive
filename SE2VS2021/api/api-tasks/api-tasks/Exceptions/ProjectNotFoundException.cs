namespace api_tasks.Exceptions;

public class ProjectNotFoundException :  Exception
{
    public ProjectNotFoundException(){}
    
    public ProjectNotFoundException(string message) :base(message){}
    
}