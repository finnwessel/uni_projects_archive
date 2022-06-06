using System.Linq;
using api_tasks.Context;
using api_tasks.Dto;
using api_tasks.Exceptions;
using api_tasks.Models;
using api_tasks.Structs;
using Microsoft.EntityFrameworkCore;

namespace api_tasks.Services;

public interface ITaskService
{
    public Task<User> FindUser(Guid userId);
    public Task<Register> FindList(Guid listId);
    public Task<List<TaskDto>> GetTasks(Guid listId);
    public Task<TaskDto?> CreateTask(NewTaskDto newTaskDto, Guid userId);
    public Task<bool> UpdateTask(TaskDto taskDto);
    Task<bool> UpdateIndex(List<SwitchedIndex> tasks);
    public Task<bool> DeleteTaskReference(Guid taskId);

}

public class TaskService : ITaskService
{
    private readonly MariaDbContext _mariaDbContext;

    public TaskService(MariaDbContext mariaDbContext)
    {
        _mariaDbContext = mariaDbContext;
    }
    
    public async Task<User> FindUser(Guid userId)
    {
        var user = await _mariaDbContext.Users.FindAsync(userId);
        if (user == null)
        {
            user = new User
            {
                Id = userId,

            };
            await _mariaDbContext.SaveChangesAsync();
        }
        return user;
    } 
    
    public async Task<Register> FindList(Guid listId)
    {
        var resultList = await _mariaDbContext.Registers.FindAsync(listId);
        if (resultList == null)
        {
            throw new ListNotFoundException("");
        }
        return resultList;
    }

    public async Task<List<TaskDto>> GetTasks(Guid listId)
    {
        var lists = await _mariaDbContext.Registers.Include(r => r.Tasks).Where(p => p.Id == listId).FirstOrDefaultAsync();
        if (lists != null)
        {
            return lists.Tasks.Select(t =>new TaskDto(t)).ToList();
        }
        return new List<TaskDto>();

    }

    public async Task<TaskDto?> CreateTask(NewTaskDto newTaskDto, Guid listId)
    {
        var task = new Tasks
        {
            Title = newTaskDto.Title,
            Index = newTaskDto.Index,
            UntilDate = newTaskDto.UntilDate,
            Description = newTaskDto.Description
        };
        var list = await FindList(listId);
        list.Tasks.Add(task);
        var result = await _mariaDbContext.SaveChangesAsync();
        if (result > 0)
        {
            return new TaskDto(task);
        }
        throw new TaskCreationException();
    }

    public async Task<bool> UpdateTask(TaskDto taskDto)
    {
        var task = await _mariaDbContext.Tasks.FindAsync(taskDto.Id);
        if (task != null)
        {
            task.Title = taskDto.Title;
            task.Index = taskDto.Index;
            task.UntilDate = taskDto.UntilDate;
            task.Description = taskDto.Description;

            return await _mariaDbContext.SaveChangesAsync() > 0;
        }

        throw new TaskNotFoundException();
    }
    
    public async Task<bool> UpdateIndex(List<SwitchedIndex> tasks)
    {
        foreach (var task in tasks)
        {
            var newTask = new Tasks
            {
                Id = task.Id,
                Index = task.Index
            };
            _mariaDbContext.Tasks.Attach(newTask);
            _mariaDbContext.Entry(newTask).Property(t => t.Index).IsModified = true;
        }
        
        return await _mariaDbContext.SaveChangesAsync() > 0;
    }
    
    public async Task<bool> DeleteTaskReference(Guid taskId)
    {
        var task = await _mariaDbContext.Tasks.FindAsync(taskId);
        if (task != null)
        {
            _mariaDbContext.Tasks.Remove(task);
        }
        return await _mariaDbContext.SaveChangesAsync() > 0;
    }
    
}