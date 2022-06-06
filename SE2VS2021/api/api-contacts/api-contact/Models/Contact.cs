using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using JsonApiDotNetCore.Resources.Annotations;

namespace api_contact.Models;

public class Contact
{
    [Key] 
    [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
    public virtual Guid? Id { get; set; }
    
    [Attr(PublicName = "contact_id")]
    public Guid? ContactId { get; set; }
    
    [Required]
    [Attr(PublicName = "user_id")]
    public Guid? UserId { get; init; }
    
    [Attr(PublicName = "firstname"), MinLength(1), MaxLength(50)]
    public string Firstname { get; set; }
    
    [Attr(PublicName = "lastname"), MinLength(1), MaxLength(50)]
    public string Lastname { get; set; }
    
    [Attr(PublicName = "birthday"), DataType(DataType.Date)]
    public string? Birthday { get; set; }
    
    [Attr(PublicName = "email"), MinLength(3), MaxLength(50)]
    public string? Email { get; set; }

    [Attr(PublicName = "address")] 
    public IList<Address> Addresses { get; set; } = new List<Address>();

    [Attr(PublicName = "phonenumber"), DataType(DataType.PhoneNumber)]
    public string? PhoneNumber { get; set; }
}