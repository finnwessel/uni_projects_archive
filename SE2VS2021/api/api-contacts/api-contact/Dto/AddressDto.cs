using api_contact.Models;

namespace api_contact.Dto;

public class AddressDto
{
    public AddressDto(Address address)
    {
        Street = address.Street;
        Number = address.Number;
        PostalCode = address.PostalCode;
        Country = address.Country;
    }
    
    public string Street { get; set; }
    
    public string Number { get; set; }
    
    public  string PostalCode { get; set; }
    
    public string City { get; set; }
    
    public string Country { get; set; }
}