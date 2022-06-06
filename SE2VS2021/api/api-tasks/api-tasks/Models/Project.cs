using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace api_tasks.Models;

public class Project
{
    [Key]
    [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
    public Guid? Id { get; init; }
    
    [Required]
    public string Title { get; set; }
    
    [Required]
    public int Index { get; set; }

    public virtual List<Register> Lists { get; set; } = new();

    public virtual List<User> User { get; set; } = new();
}