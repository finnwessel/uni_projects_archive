using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace api_tasks.Models;

public class Tasks
{
    [Key] 
    [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
    public Guid? Id { get; init; }
    
    [Required]
    public string? Title { get; set; }
    
    [Required]
    public int Index { get; set; }
    
    public string? UntilDate { get; set; }
    
    [Required]
    public string? Description { get; set; }
    
    public Register List { get; set; } = null!;
}