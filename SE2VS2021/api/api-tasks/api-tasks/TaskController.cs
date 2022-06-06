using Microsoft.AspNetCore.Mvc;

namespace api_tasks;

public interface ITaskController
{
    
}
[ApiController]
[Route("[controller]")]
public class TaskController : ControllerBase, ITaskController
{
    
}