using api_contact.Dto;
using api_contact.Helpers;
using api_contact.Services;
using Microsoft.AspNetCore.Diagnostics;
using Microsoft.AspNetCore.Mvc;
using Microsoft.IdentityModel.Tokens;
using RpcCommunication;
using Swashbuckle.AspNetCore.Annotations;

namespace api_contact.Controllers;

public interface IContactController
{
    Task<ActionResult<IEnumerable<ContactDto>>> GetAll();

    Task<ActionResult<ContactDto>> GetContact(Guid id);

    Task<ActionResult<IEnumerable<ContactDto>>> GetByName(String firstname, String lastname);

    Task<ActionResult<ContactDto?>> UpdateContact(Guid id, ContactDto contactDto);

    Task<ActionResult<Guid?>> CreateContact(NewContactDto contactDto);

    Task<ActionResult<ContactDto?>> DeleteContact(Guid id);
}

[ApiController]
[Route("[controller]")]
public class ContactController : Controller, IContactController
{
    private readonly IContactService _contactService;
    private readonly IRpcClient _rpcService;

    public ContactController(IContactService contactService, IRpcClient rpcService)
    {
        _contactService = contactService;
        _rpcService = rpcService;
    }

    [SwaggerResponse(StatusCodes.Status200OK, "Returns all existing contacts")]
    [HttpGet]
    [Auth]
    public async Task<ActionResult<IEnumerable<ContactDto>>> GetAll()
    {
        var userId = (Guid?)HttpContext.Items["AuthUserId"];
        if (userId == null)
        {
            return BadRequest("No valid user id provided in authentication.");
        }
        return Ok(await _contactService.GetAll(userId));
    }

    [SwaggerResponse(StatusCodes.Status200OK, "Returns contact with given id")]
    [SwaggerResponse(StatusCodes.Status404NotFound, "No contact with given id")]
    [HttpGet("{id}")]
    public async Task<ActionResult<ContactDto>> GetContact(Guid id)
    {
        var userId = (Guid?)HttpContext.Items["AuthUserId"];
        if (userId == null)
        {
            return BadRequest("No valid user id provided in authentication.");
        }
        var contact = await _contactService.GetById(id, userId);

        if (contact == null)
        {
            return NotFound();
        }
        
        return Ok(contact);
    }
    
    [SwaggerResponse(StatusCodes.Status200OK, "Returns contacts with given firstname or lastname")]
    [SwaggerResponse(StatusCodes.Status404NotFound, "No contact with given firstname or lastname")]
    [HttpGet("{firstname}, {lastname}")]
    [Auth]
    public async Task<ActionResult<IEnumerable<ContactDto>>> GetByName(string firstname, string lastname)
    {
        var userId = (Guid?)HttpContext.Items["AuthUserId"];
        if (userId == null)
        {
            return BadRequest("No valid user id provided in authentication.");
        }
        var contacts = await _contactService.GetByName(firstname, lastname, userId);
        if (contacts.IsNullOrEmpty())
        {
            return NotFound();
        }

        return Ok(contacts);
    }

    [SwaggerResponse(StatusCodes.Status204NoContent, "Updated contact")]
    [SwaggerResponse(StatusCodes.Status400BadRequest, "Failed to update contact")]
    [HttpPut("{id}")]
    public async Task<ActionResult<ContactDto?>> UpdateContact(Guid id, ContactDto contactDto)
    {
        var userId = (Guid?)HttpContext.Items["AuthUserId"];
        if (userId == null)
        {
            return BadRequest("No valid user id provided in authentication.");
        }
        if (id != contactDto.Id)
        {
            return BadRequest();
        }

        var contact = await _contactService.UpdateContact(id, contactDto, userId);
        if (contact == null)
        {
            return BadRequest();
        }

        return NoContent();
    }

    [SwaggerResponse(StatusCodes.Status201Created, "Created contact")]
    [SwaggerResponse(StatusCodes.Status400BadRequest, "Failed to create contact")]
    [HttpPost]
    [Auth]
    public async Task<ActionResult<Guid?>> CreateContact(NewContactDto contactDto)
    {
        var userId = (Guid?)HttpContext.Items["AuthUserId"];
        if (userId == null)
        {
            return BadRequest("No valid user id provided in authentication.");
        }

        var contactId = await _contactService.CreateContact(contactDto, userId);
        if (contactId == null)
        {
            return BadRequest();
        }

        return Ok(contactId.Id);
    }

    [SwaggerResponse(StatusCodes.Status200OK, "Deleted contact")]
    [SwaggerResponse(StatusCodes.Status404NotFound, "No contact exists with given id")]
    [SwaggerResponse(StatusCodes.Status400BadRequest, "Failed to delete contact")]
    [HttpDelete("{id}")]
    public async Task<ActionResult<ContactDto?>> DeleteContact(Guid id)
    {
        var userId = (Guid?)HttpContext.Items["AuthUserId"];
        if (userId == null)
        {
            return BadRequest("No valid user id provided in authentication.");
        }
        var contact = await _contactService.GetById(id, userId);
        if (contact == null)
        {
            return NotFound();
        }

        var deletedContact = await _contactService.DeleteContact(contact, userId);
        if (deletedContact == null)
        {
            return BadRequest();
        }

        return Ok(deletedContact);
    }

    [Auth]
    [HttpGet("search")]
    public async Task<ActionResult<List<ContactDto>>> GetUserSearch([FromQuery] string query)
    {
        var userId = (Guid?)HttpContext.Items["AuthUserId"];
        if (userId == null)
        {
            return BadRequest("No valid user id provided in authentication.");
        }

        var contactIdList = await _contactService.GetAllIds(userId);
        var queryResult = _rpcService.SearchUser(query).Users.Where(u => !contactIdList.Contains(u.Id) && u.Id != userId);

       var users = new List<ContactDto>();
       foreach (var user in queryResult)
       {
           users.Add(new ContactDto
           {
               Id = user.Id,
               Firstname = user.Firstname,
               Lastname = user.Lastname
           });
       }
       return Ok(users);
    }

    [Auth]
    [HttpPost("{contactId}")]
    public async Task<ActionResult<ContactDto>> CreateContactByUserId(Guid contactId)
    {
        var userId = (Guid?)HttpContext.Items["AuthUserId"];
        
        if (userId == null)
        {
            return BadRequest("No valid user id provided in authentication.");
        }

        var contact = _rpcService.GetUserDetails(contactId).UserDetails;

        var newContact = new NewContactDto
        {
            ContactId = contact.Id,
            Firstname = contact.Firstname,
            Lastname = contact.Lastname,
            Email = contact.Email
        };

        var createdContact = await _contactService.CreateContact(newContact, userId);

        if (createdContact == null)
        {
            return StatusCode(500);
        }

        return Ok(createdContact);
    }
}