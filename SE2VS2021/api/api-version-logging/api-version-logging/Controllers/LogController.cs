using api_version_logging.Services;
using JwtMiddleware.Helpers;
using Microsoft.AspNetCore.Mvc;

namespace api_version_logging.Controllers;

public class LogController : ControllerBase
{
    private readonly ILogService _logService;

    public LogController(ILogService logService)
    {
        _logService = logService;
    }
    
    [Auth]
    [HttpGet]
    [Route("system/{name}")]
    public async Task<IActionResult> GetSystemLog(string name)
    {
        return Ok(await _logService.GetSystemLog(name));
    }

    [Auth]
    [HttpGet]
    [Route("user")]
    public async Task<IActionResult> GetUserLog()
    {
        var userId = (Guid?) HttpContext.Items["AuthUserId"];
        if (userId == null)
        {
            return BadRequest("No valid user id provides in authentication.");
        }
        return Ok(await _logService.GetUserLog(userId.Value));
    }
    
    [Auth]
    [HttpGet]
    [Route("reference/{id}")]
    public async Task<IActionResult> GetLogByReferenceId(Guid id)
    {
        return Ok(await _logService.GetLogByReferenceId(id));
    }
}