namespace api_version_logging.Dto;

public class NewLogDto
{
    public string System { get; set; }
    public Guid? User { get; set; }
    public LogDto Log { get; set; }
}