using api_tasks.Models;

namespace api_tasks.Dto;

public class RegisterDto : NewRegisterDto
{   
    public RegisterDto(){}

    public RegisterDto(Register register)
    {
        Id = register.Id;
        Title = register.Title;
        Index = register.Index;
    }
    public Guid? Id { get; set; }
}