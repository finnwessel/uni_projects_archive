using api_version_logging.Context;
using api_version_logging.Dto;
using api_version_logging.Models;
using Microsoft.EntityFrameworkCore;

namespace api_version_logging.Services;

public interface ILogService
{
    Task<List<LogDto>> GetUserLog(Guid id);
    Task<List<LogDto>> GetSystemLog(string name);
    Task<List<LogDto>> GetLogByReferenceId(Guid id);
    Task AddNewLogEntry(NewLogDto logDto);
}

public class LogService: ILogService
{
    private readonly MariaDbContext _context;

    public LogService(MariaDbContext context)
    {
        _context = context;
    }
    
    public async Task<List<LogDto>> GetUserLog(Guid id)
    {
        return await _context.Logs.Where(l => l.UserId == id).Select(l => new LogDto(l)).ToListAsync();
    }
    
    public async Task<List<LogDto>> GetSystemLog(string name)
    {
        return await _context.Logs.Where(l => l.System == name).Select(l => new LogDto(l)).ToListAsync();
    }
    
    public async Task<List<LogDto>> GetLogByReferenceId(Guid id)
    {
        return await _context.Logs.Where(l => l.ReferenceId == id).Select(l => new LogDto(l)).ToListAsync();
    }
    public async Task AddNewLogEntry(NewLogDto logDto)
    {

        var log = new Log
        {
            UserId = logDto.User,
            System = logDto.System,
            ReferenceId = logDto.Log.ReferenceId,
            Message = logDto.Log.Message,
            TimeStamp = logDto.Log.TimeStamp
        };
        _context.Logs.Add(log);
        
        await _context.SaveChangesAsync();
    }
}