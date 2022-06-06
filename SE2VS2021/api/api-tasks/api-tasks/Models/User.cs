using System.ComponentModel.DataAnnotations;

namespace api_tasks.Models;

public class User
{
    [Key] 
    public Guid? Id { get; init; }
    
    public List<Project> Projects { get; set; } = new();
}