using api_tasks.Dto;
using api_tasks.Services;
using api_tasks.Structs;
using JwtMiddleware.Helpers;
using Microsoft.AspNetCore.Mvc;
using VersionLogging;

namespace api_tasks.Controllers;

[ApiController]
[Route("[controller]")]
public class TaskController : ControllerBase, ITaskController
{
    private readonly ITaskService _tasksService;
    private readonly IVersionLogPublisher _logPublisher;

    public TaskController(ITaskService taskService, IVersionLogPublisher logPublisher)
    {
        _tasksService = taskService;
        _logPublisher = logPublisher;
    }

    [Auth]
    [HttpGet]
    [Route("{listId}")]
    public async Task<IActionResult> GetTasks(Guid listId)
    {
        return Ok(await _tasksService.GetTasks(listId));
    }
    
    
    [Auth]
    [HttpPost]
    [Route("{listId}")]
    public async Task<IActionResult> CreateTask(string listId,[FromBody] NewTaskDto newTaskDto)
    {
        var userId = (Guid?) HttpContext.Items["AuthUserId"];
        if (userId == null)
        {
            return BadRequest("No valid user id provides in authentication.");
        }
        Guid parsedListId;
        try
        {
            parsedListId = Guid.Parse(listId);
        }
        catch 
        {
            return BadRequest("no valid listId");
        }

        var task = await _tasksService.CreateTask(newTaskDto, parsedListId);
        if(task == null)
        {
            return BadRequest("can not create task");
        }
        
        if (task.Id != null)
        {
            _logPublisher.Log(task.Id.Value, $"Task: {task.Title} created.", "tasks", userId);    
        }
        
        return Created("", task);
    }
    
    [Auth]
    [HttpPut]
    public async Task<IActionResult> UpdateTask(TaskDto taskDto)
    {
        var task = await _tasksService.UpdateTask(taskDto);
        if(!task)
        {
            return BadRequest("can not update task");
        }

        return Ok();
    }
    
    [Auth]
    [HttpPut("index")]
    public async Task<IActionResult> UpdateRegister([FromBody] List<SwitchedIndex> tasks)
    {
        var result = await _tasksService.UpdateIndex(tasks);
        if (!result)
        {
            return BadRequest("can not update indexes");
        }

        return Ok();
    }

    [Auth]
    [HttpDelete]
    [Route("{taskId}")]
    public async Task<IActionResult> DeleteTask(Guid taskId)
    {
        var task = await _tasksService.DeleteTaskReference(taskId);
        if(!task)
        {
            return BadRequest("can not delete task");
        }

        return Ok();
    }
}