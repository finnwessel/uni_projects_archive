using api_tasks.Models;

namespace api_tasks.Dto;

public class ProjectDto : NewProjectDto
{
    public Guid? Id { get; set; } 

    
    public ProjectDto() {}
    public ProjectDto(Project project)
    {
        Id = project.Id;
        Title = project.Title;
        Index = project.Index;
    }

}