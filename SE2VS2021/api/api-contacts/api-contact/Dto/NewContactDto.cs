using System.ComponentModel.DataAnnotations;

namespace api_contact.Dto;

public class NewContactDto
{
 public Guid? ContactId { get; set; }
 
    [Required,
     DataType(DataType.Text),
     MinLength(1),
     MaxLength(50)]
    public string Firstname { get; set; }
    
    [Required,
     DataType(DataType.Text),
     MinLength(1),
     MaxLength(50)]
    public string Lastname { get; set; }
    
    [DataType(DataType.Date),
    MinLength(1),
    MaxLength(50)]
    public string Birthday { get; set; }


    [DataType(DataType.Text),
     MinLength(3),
     MaxLength(50),
     RegularExpression("^(.+)@(.+)$", ErrorMessage = "Must be valid email format")]
    public string Email { get; set; }

    public IList<AddressDto> Addresses { get; set; } = new List<AddressDto>();
    
    [DataType(DataType.PhoneNumber),
     MinLength(1),
     MaxLength(50)]
    public string PhoneNumber { get; set; }
}