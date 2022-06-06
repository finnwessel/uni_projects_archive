namespace api_storage.Structs;

public struct FileList
{
    public Guid OwnerId { get; init; }
    public List<FileData> Files { get; } = new();
}