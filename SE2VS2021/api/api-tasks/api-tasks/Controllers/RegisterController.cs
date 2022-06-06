using api_tasks.Dto;
using api_tasks.Services;
using api_tasks.Structs;
using JwtMiddleware.Helpers;
using Microsoft.AspNetCore.Mvc;
using VersionLogging;

namespace api_tasks.Controllers;

[ApiController]
[Route("[controller]")]
public class RegisterController :ControllerBase
{
    private readonly IRegisterService _registerService;
    private readonly IVersionLogPublisher _logPublisher;

    public RegisterController(IRegisterService registerService, IVersionLogPublisher logPublisher)
    {
        _registerService = registerService;
        _logPublisher = logPublisher;
    }

    [Auth]
    [HttpGet("{projectId}")]
    public async Task<IActionResult> GetRegister(Guid projectId)
    {
        return Ok(await _registerService.GetRegisters(projectId));
    }

    [Auth]
    [HttpPost("{projectId}")]
    public async Task<IActionResult> CreateRegister(string projectId, [FromBody] NewRegisterDto newRegisterDto)
    {
        var userId = (Guid?) HttpContext.Items["AuthUserId"];
        if (userId == null)
        {
            return BadRequest("No valid user id provides in authentication.");
        }
        
        Guid parsedProjectId;
        try
        {
            parsedProjectId = Guid.Parse(projectId);
        }
        catch
        {
            return BadRequest("no valid projectId");
        }

        var register = await _registerService.CreateRegister(newRegisterDto, parsedProjectId);
        if (register == null)
        {
            return BadRequest("can not create register");
        }

        if (register.Id != null)
        {
            _logPublisher.Log(register.Id.Value, $"List: {register.Title} created.", "tasks", userId);    
        }

        return Created("", register);
    }

    [Auth]
    [HttpPut]
    public async Task<IActionResult> UpdateRegister([FromBody] RegisterDto registerDto)
    {
        var userId = (Guid?) HttpContext.Items["AuthUserId"];
        if (userId == null)
        {
            return BadRequest("No valid user id provides in authentication.");
        }
        
        var register = await _registerService.UpdateRegister(registerDto);
        if (!register)
        {
            return BadRequest("can not update register");
        }
        
        if (registerDto.Id != null)
        {
            _logPublisher.Log(registerDto.Id.Value, "List updated.", "tasks", userId);    
        }

        return Ok();
    }
    
    [Auth]
    [HttpPut("index")]
    public async Task<IActionResult> UpdateRegister([FromBody] List<SwitchedIndex> registers)
    {
        var result = await _registerService.UpdateIndex(registers);
        if (!result)
        {
            return BadRequest("can not update indexes");
        }

        return Ok();
    }

    [Auth]
    [HttpDelete("{registerId}")]
    public async Task<IActionResult> DeleteRegister(Guid registerId)
    {
        var userId = (Guid?) HttpContext.Items["AuthUserId"];
        if (userId == null)
        {
            return BadRequest("No valid user id provides in authentication.");
        }
        var register = await _registerService.DeleteRegisterReference(registerId);
        if (!register)
        {
            return BadRequest("can not delete register");
        }
        _logPublisher.Log(registerId, "List deleted.", "tasks", userId);  
        return Ok();
    }
}