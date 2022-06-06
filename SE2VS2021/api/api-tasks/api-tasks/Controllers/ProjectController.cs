using api_tasks.Dto;
using api_tasks.Services;
using api_tasks.Structs;
using JwtMiddleware.Helpers;
using Microsoft.AspNetCore.Mvc;
using VersionLogging;

namespace api_tasks.Controllers;

public interface IProjectController
{
    public Task<IActionResult> GetProjects();
    public Task<IActionResult> CreateProject([FromBody] NewProjectDto newProjectDto);
    public Task<IActionResult> UpdateProject([FromBody] ProjectDto projectDto);
    public Task<IActionResult> DeleteProject([FromBody] ProjectDto projectDto);
    
}

[ApiController]
[Route("[controller]")]

public class ProjectController : ControllerBase
{
    private readonly IProjectService _projectService;
    private readonly IVersionLogPublisher _logPublisher;
    public ProjectController(IProjectService projectService, IVersionLogPublisher logPublisher)
    {
        _projectService = projectService;
        _logPublisher = logPublisher;
    }
    
    [Auth]
    [HttpGet]
    public async Task<IActionResult> GetProjects()
    {
        var userId = (Guid?) HttpContext.Items["AuthUserId"];
        if (userId == null)
        {
            return BadRequest("No valid user id provides in authentication.");
        }

        return Ok(await _projectService.GetProjects(userId.Value));
    }
    
    [Auth]
    [HttpPost]
    public async Task<IActionResult> CreateProject([FromBody] NewProjectDto newProjectDto)
    {
        var userId = (Guid?) HttpContext.Items["AuthUserId"];
        if (userId == null)
        {
            return BadRequest("No valid user id provides in authentication.");
        }
        var project = await _projectService.CreateProject(newProjectDto, userId.Value); 
        if(project == null)
        {
            return BadRequest("can not create. sorry.");
        }

        if (project.Id != null)
        {
            _logPublisher.Log(project.Id.Value, $"Project: {project.Title} created.", "tasks", userId);   
        }
        return Created("", project);
    }
    
    [Auth]
    [HttpPut]
    public async Task<IActionResult> UpdateProject([FromBody] ProjectDto projectDto)
    {
        var userId = (Guid?) HttpContext.Items["AuthUserId"];
        if (userId == null)
        {
            return BadRequest("No valid user id provides in authentication.");
        }
        if(await _projectService.UpdateProject(projectDto))
        {
            if (projectDto.Id != null)
            {
                _logPublisher.Log(projectDto.Id.Value, "Project updated.", "tasks", userId);   
            }
            return Ok();
        }
        return BadRequest("can not update. sorry.");
    }
    
    [Auth]
    [HttpPut("index")]
    public async Task<IActionResult> UpdateRegister([FromBody] List<SwitchedIndex> projects)
    {
        var result = await _projectService.UpdateIndex(projects);
        if (!result)
        {
            return BadRequest("can not update indexes");
        }

        return Ok();
    }
    
    [Auth]
    [HttpDelete("{projectId}")]
    public async Task<IActionResult> DeleteProject(Guid projectId)
    {
        var userId = (Guid?) HttpContext.Items["AuthUserId"];
        if (userId == null)
        {
            return BadRequest("No valid user id provides in authentication.");
        }
         
        if(await _projectService.DeleteProjectReference(projectId, userId.Value))
        {
            _logPublisher.Log(projectId, "Project deleted.", "tasks", userId);
            return Ok();
        }
        return BadRequest("can not delete project");
    }
}