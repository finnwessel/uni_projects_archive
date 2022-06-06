using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace api_tasks.Models;

public class Register
{
    [Key] 
    [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
    public Guid? Id { get; init; }

    public string? Title { get; set; }
    
    public int Index { get; set; }

    public List<Tasks> Tasks { get; set; } = new();

    public Project Project { get; set; } = null!;
}