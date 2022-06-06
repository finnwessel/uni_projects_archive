namespace VersionLogging.Dto;

public class VersionLogDto
{
    public Guid ReferenceId { get; set; }
    public string Message { get; set; }
    public DateTime TimeStamp { get; set; }
    
    public VersionLogDto(Guid referenceId, string message, DateTime timeStamp)
    {
        ReferenceId = referenceId;
        Message = message;
        TimeStamp = timeStamp;
    }
}