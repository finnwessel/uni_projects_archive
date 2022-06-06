using api_tasks.Context;
using api_tasks.Dto;
using api_tasks.Exceptions;
using api_tasks.Models;
using api_tasks.Structs;
using Microsoft.EntityFrameworkCore;

namespace api_tasks.Services;

public interface IProjectService
{
    Task<User> FindUser(Guid userId);
    Task<List<ProjectDto>> GetProjects(Guid userId);
    Task<ProjectDto?> CreateProject(NewProjectDto newProjectDto, Guid userId);
    Task<bool> UpdateProject(ProjectDto projectDto);
    Task<bool> UpdateIndex(List<SwitchedIndex> projects);
    Task<bool> DeleteProjectReference(Guid projectId, Guid userId);

}

public class ProjectService : IProjectService
{
    private readonly MariaDbContext _mariaDbContext;
    public ProjectService(MariaDbContext mariaDbContext)
    {
        _mariaDbContext = mariaDbContext;
    }

    public async Task<User> FindUser(Guid userId)
    {
        var user = await _mariaDbContext.Users.Include(u => u.Projects).Where(u => u.Id == userId).FirstOrDefaultAsync();
        if (user == null)
        {
            user = new User
            {
                Id = userId,
            };
            _mariaDbContext.Users.Add(user);
        }
        return user;
    } 
    
    
    public async Task<List<ProjectDto>> GetProjects(Guid userId)
    {
        var user = await FindUser(userId);
        return user.Projects.Select(p => new ProjectDto(p)).ToList();
    }

    public async Task<ProjectDto?> CreateProject(NewProjectDto newProjectDto, Guid userId)
    {
        var project = new Project
        {
            Title = newProjectDto.Title,
            Index = newProjectDto.Index,
        };

        var user = await FindUser(userId);

        user.Projects.Add(project);

        var result = await _mariaDbContext.SaveChangesAsync();
        if (result > 0)
        {
            return new ProjectDto(project);
        }

        throw new ProjectCreationException();

    }
    
    public async Task<bool> UpdateProject(ProjectDto projectDto)
    {

        var project = await _mariaDbContext.Projects.FindAsync(projectDto.Id);
        if (project == null)
        {
            return false;
        }

        project.Index = projectDto.Index;
        project.Title = projectDto.Title;

        _mariaDbContext.Projects.Update(project);
        
        return await _mariaDbContext.SaveChangesAsync() > 0;
    }
    
    public async Task<bool> UpdateIndex(List<SwitchedIndex> projects)
    {
        foreach (var project in projects)
        {
            var newProject = new Project
            {
                Id = project.Id,
                Index = project.Index
            };
            _mariaDbContext.Projects.Attach(newProject);
            _mariaDbContext.Entry(newProject).Property(p => p.Index).IsModified = true;
        }

        return await _mariaDbContext.SaveChangesAsync() > 0;
    }

    public async Task<bool> DeleteProjectReference(Guid projectId, Guid userId)
    {
        var user = await FindUser(userId);
        user.Projects.RemoveAll(p => p.Id == projectId);
        
        return await _mariaDbContext.SaveChangesAsync() > 0;
    }
    
    
}