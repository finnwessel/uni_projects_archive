using api_tasks.Context;
using api_tasks.Dto;
using api_tasks.Exceptions;
using api_tasks.Models;
using api_tasks.Structs;
using Microsoft.EntityFrameworkCore;

namespace api_tasks.Services;

public interface IRegisterService
{
    public Task<List<RegisterDto>> GetRegisters(Guid projectId);
    public Task<RegisterDto?> CreateRegister(NewRegisterDto newRegisterDto, Guid projectId);
    public Task<bool> UpdateRegister(RegisterDto registerDto);
    public Task<bool> UpdateIndex(List<SwitchedIndex> registers);
    public Task<bool> DeleteRegisterReference(Guid registerId);
}



public class RegisterService : IRegisterService
{
    private readonly MariaDbContext _mariaDbContext;

    public RegisterService(MariaDbContext mariaDbContext)
    {
        _mariaDbContext = mariaDbContext;
    }

    public async Task<List<RegisterDto>> GetRegisters(Guid projectId)
    {
        var project = await _mariaDbContext.Projects.Include(p => p.Lists).Where(p => p.Id == projectId).FirstOrDefaultAsync();
        if (project != null)
        {
            return project.Lists.Select(p => new RegisterDto(p)).ToList();
        }

        return new List<RegisterDto>();
    }

    public async Task<RegisterDto?> CreateRegister(NewRegisterDto newRegisterDto, Guid projectId)
    {
        var register = new Register
        {
            Title = newRegisterDto.Title,
            Index = newRegisterDto.Index,
        };
        var project = await _mariaDbContext.Projects.FindAsync(projectId);

        project?.Lists.Add(register);

        var result = await _mariaDbContext.SaveChangesAsync();
        if (result > 0)
        {
            return new RegisterDto(register);
        }

        throw new ListCreationException();
    }

    public async Task<bool> UpdateRegister(RegisterDto registerDto)
    {
        var register = await _mariaDbContext.Registers.FindAsync(registerDto.Id);
        
        if (register != null)
        {
            register.Title = registerDto.Title;
            register.Index = registerDto.Index;
            _mariaDbContext.Update(register);
            return await _mariaDbContext.SaveChangesAsync() > 0;
        }

        throw new ListNotFoundException();
    }

    public async Task<bool> UpdateIndex(List<SwitchedIndex> registers)
    {
        foreach (var register in registers)
        {
            var newRegister = new Register
            {
                Id = register.Id,
                Index = register.Index
            };
            _mariaDbContext.Registers.Attach(newRegister);
            _mariaDbContext.Entry(newRegister).Property(r => r.Index).IsModified = true;
        }

        return await _mariaDbContext.SaveChangesAsync() > 0;
    }

    public async Task<bool> DeleteRegisterReference(Guid registerId)
    {
        var list = await _mariaDbContext.Registers.FindAsync(registerId);
        if (list != null)
        {
            _mariaDbContext.Registers.Remove(list);
        }

        return await _mariaDbContext.SaveChangesAsync() > 0;
    }


}