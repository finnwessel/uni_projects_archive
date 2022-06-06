using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace api_version_logging.Models;

public class Log
{
    [Key]
    [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
    public Guid Id { get; init; }
    public Guid ReferenceId { get; set; }
    public Guid? UserId { get; set; }
    public string System { get; set; }
    public string Message { get; set; }
    public DateTime TimeStamp { get; set; }
}