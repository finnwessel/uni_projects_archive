using System;

namespace api_storage.Dto
{
    public class OwnFilesList
    {
        public Guid FileId { get; }

        public OwnFilesList(Guid fileId)
        {
            FileId = fileId;
        }
    }
}