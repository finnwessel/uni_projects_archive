using System.ComponentModel.DataAnnotations;
using System.Data.Entity.Core.Mapping;
using api_contact.Models;

namespace api_contact.Dto;

public class ContactDto
{
    
    public ContactDto() {}
    public ContactDto(Contact contact)
    {
        Id = contact.Id;
        ContactId = contact.ContactId;
        Firstname = contact.Firstname;
        Lastname = contact.Lastname;
        Birthday = contact.Birthday;
        Email = contact.Email;
        PhoneNumber = contact.PhoneNumber;
        Addresses = contact.Addresses.Select(a => new AddressDto(a)).ToList();
    }
    
    public Guid? Id { get; set; }
    
    public Guid? ContactId { get; set; }
    
    [Required]
    public string? Firstname { get; set; }
    
    [Required] 
    public string? Lastname { get; set; }
    
    public string? Birthday { get; set; }
    
    public string? Email { get; set; }

    public IList<AddressDto> Addresses { get; set; } = new List<AddressDto>();
    
    public string? PhoneNumber { get; set; }
}