namespace api_tasks.Dto;

public class NewTaskDto
{
    public string Title { get; set; }
    public int Index { get; set; }
    public string? UntilDate { get; set; }
    public string Description { get; set; }
}