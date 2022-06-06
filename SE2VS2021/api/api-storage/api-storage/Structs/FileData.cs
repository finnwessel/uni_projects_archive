namespace api_storage.Structs;

public struct FileData
{
    public bool IsPublic { get; init; }
    public string RelativeFilePath { get; init; } = null!;
}