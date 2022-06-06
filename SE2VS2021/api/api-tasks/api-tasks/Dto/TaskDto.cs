using api_tasks.Models;

namespace api_tasks.Dto;

public class TaskDto : NewTaskDto
{
    public Guid? Id { get; set; }

    public TaskDto()
    {
        
    }
    public TaskDto(Tasks task)
    {
        Id = task.Id;
        Title = task.Title;
        Index = task.Index;
        UntilDate = task.UntilDate;
        Description = task.Description;
    }
}