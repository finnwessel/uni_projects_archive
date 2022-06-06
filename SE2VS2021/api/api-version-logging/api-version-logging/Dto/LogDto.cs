using api_version_logging.Models;

namespace api_version_logging.Dto;

public class LogDto
{
    public Guid ReferenceId { get; set; }
    public string Message { get; set; } = null!;
    public DateTime TimeStamp { get; set; }

    public LogDto()
    {
        
    }

    public LogDto(Log log)
    {
        ReferenceId = log.ReferenceId;
        Message = log.Message;
        TimeStamp = log.TimeStamp;
    }
}