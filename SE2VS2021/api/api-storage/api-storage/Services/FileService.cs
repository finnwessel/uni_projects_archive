using api_storage.Context;
using api_storage.Dto;
using api_storage.Models;
using api_storage.Structs;
using Microsoft.EntityFrameworkCore;

namespace api_storage.Services
{
    public interface IFileService
    {
        Task<IList<OwnFilesList>> GetOwnFiles(Guid ownerId);
        Task<List<Guid>?> AddUploadedFile(FileList fileList);
        Task<UploadedFile?> GetUploadedFile(Guid fileId);
        void DeleteFile(Guid fileId);
    }
    public class FileService : IFileService
    {
        private readonly MariaDbContext _context;
        
        public FileService(MariaDbContext context)
        {
            _context = context;
        }

        public void DeleteFile(Guid fileId)
        {
            UploadedFile file = new UploadedFile()
            {
                Id = fileId
            };
            _context.UploadedFiles.Attach(file);
            _context.UploadedFiles.Remove(file);
        }

        public async Task<IList<OwnFilesList>> GetOwnFiles(Guid ownerId)
        {
            return await _context.UploadedFiles.Where(f => f.OwnerId == ownerId).Select(f => new OwnFilesList(f.Id)).ToListAsync();
        }
        public async Task<List<Guid>?> AddUploadedFile(FileList list)
        {
            var resultIds = new List<Guid>();
            foreach (var uploadedFile in list.Files.Select(file => new UploadedFile
                     {
                         OwnerId = list.OwnerId,
                         Public = file.IsPublic,
                         RelativeFilePath = file.RelativeFilePath
                     }))
            {
                _context.UploadedFiles.Add(uploadedFile);
                resultIds.Add(uploadedFile.Id);
            }
            
            var result = await _context.SaveChangesAsync();
            return result > 0 ? resultIds : null;
        }
        public async Task<UploadedFile?> GetUploadedFile(Guid fileId)
        {
            return await _context.UploadedFiles.FindAsync(fileId);
        }
    }
}