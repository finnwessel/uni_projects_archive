namespace VersionLogging.Dto;

public class NewVersionLogDto
{
    public string System { get; set; }
    public Guid? User { get; set; }
    public VersionLogDto Log { get; set; }

    public NewVersionLogDto(string system, Guid? userId, VersionLogDto versionLogDto)
    {
        System = system;
        User = userId;
        Log = versionLogDto;
    }
}