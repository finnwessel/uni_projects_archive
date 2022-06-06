using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using JsonApiDotNetCore.Resources.Annotations;

namespace api_contact.Models;

public class Address
{
    [Key] 
    [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
    public Guid? Id { get; init; }
    
    [Attr(PublicName = "street"), MinLength(1), MaxLength(50)]
    public string Street { get; set; }
    
    [Attr(PublicName = "number"), MinLength(0), MaxLength(50)]
    public string Number { get; set; }
    
    [Attr(PublicName = "postalcode"), DataType(DataType.PostalCode)]
    public  string PostalCode { get; set; }
    
    [Attr(PublicName = "city"), MinLength(1), MaxLength(50)]
    public string City { get; set; }
    
    [Attr(PublicName = "country"), MinLength(1), MaxLength(50)]
    public string Country { get; set; }
}