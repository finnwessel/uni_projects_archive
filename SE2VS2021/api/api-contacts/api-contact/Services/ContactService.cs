using api_contact.Context;
using api_contact.Dto;
using api_contact.Models;
using Microsoft.EntityFrameworkCore;

namespace api_contact.Services

{
    public interface IContactService
    {
        Task<List<ContactDto>> GetAll(Guid? userId);
        Task<List<Guid?>> GetAllIds(Guid? userId);
        Task<ContactDto?> GetById(Guid id, Guid? userId);

        Task<List<ContactDto?>> GetByName(String? firstname, String? lastname, Guid? userId);
        Task<ContactDto?> UpdateContact(Guid id, ContactDto contactDto, Guid? userId);
        Task<ContactDto?> CreateContact(NewContactDto contactDto, Guid? userId);
        Task<ContactDto?> DeleteContact(ContactDto contactDto, Guid? userId);
    }

    public class ContactService : IContactService
    {
        private readonly MariaDbContext _context;

        public ContactService(MariaDbContext context)
        {
            _context = context;
        }

        public async Task<List<ContactDto>> GetAll(Guid? userId)
        {
            return await _context.Contacts.Where(c => c.UserId == userId)
                .Select(contact => new ContactDto(contact)).AsNoTracking().ToListAsync();
        }
        
        public async Task<List<Guid?>> GetAllIds(Guid? userId)
        {
            var contactIdList = new List<Guid?>();
            await _context.Contacts.Where(c => c.UserId == userId)
                .ForEachAsync(contact => contactIdList.Add(contact.ContactId));
            return contactIdList;
        }

        public async Task<ContactDto?> GetById(Guid id, Guid? userId)
        {
            return await _context.Contacts.Where(contact => contact.UserId == userId)
                .Select(contact => new ContactDto(contact))
                .Where(contactDto => contactDto.Id == id).AsNoTracking().FirstOrDefaultAsync();
        }

        public async Task<List<ContactDto?>> GetByName(string? firstname, string? lastname, Guid? userId)
        {
            return await _context.Contacts.Where(contact => contact.UserId == userId)
                .Select(contact => new ContactDto(contact))
                .Where(contactDto => (contactDto.Firstname.Contains(firstname) || contactDto.Lastname.Contains(lastname))).AsNoTracking().ToListAsync();
        }

        public async Task<ContactDto?> UpdateContact(Guid id, ContactDto contactDto, Guid? userId)
        {
            var contact = _context.Contacts.SingleOrDefault(c => c.Id == id && c.UserId == userId);
            if (contact == null)
            {
                return null;
            }

            contact.Firstname = contactDto.Firstname;
            contact.Lastname = contactDto.Lastname;
            contact.Birthday = contactDto.Birthday;
            contact.Email = contactDto.Email;
            contact.Addresses = new List<Address>(
                from a in contact.Addresses
                select new Address
                    {Country = a.Country, Number = a.Number, Street = a.Street, PostalCode = a.PostalCode, City = a.City}
            );
            contact.PhoneNumber = contactDto.PhoneNumber;
            

            _context.Entry(contact).State = EntityState.Modified;

            var result = await _context.SaveChangesAsync();

            return (result > 0) ? contactDto : null;
        }

        public async Task<ContactDto?> CreateContact(NewContactDto contactDto, Guid? userId)
        {

            var contact = new Contact
            {
                UserId = userId, 
                ContactId = contactDto.ContactId,
                Firstname = contactDto.Firstname,
                Lastname = contactDto.Lastname,
                Birthday = contactDto.Birthday, 
                Email = contactDto.Email,
                PhoneNumber = contactDto.PhoneNumber
            };
            
            foreach (var address in contactDto.Addresses)
            {
                contact.Addresses.Add(new Address
                {
                    Country = address.Country, 
                    Number = address.Number, 
                    Street = address.Street, 
                    PostalCode = address.PostalCode, 
                    City = address.City
                });
            }
            
            _context.Contacts.Add(contact);
            var result = await _context.SaveChangesAsync();
            return result > 0 ? new ContactDto(contact) : null;
        }

        public async Task<ContactDto?> DeleteContact(ContactDto contactDto, Guid? userId)
        {
            var contact = new Contact()
            {
                Id = contactDto.Id,
                UserId = userId, 
                Firstname = contactDto.Firstname,
                Lastname = contactDto.Lastname, 
                Birthday = contactDto.Birthday, 
                Email = contactDto.Email,
                Addresses = new List<Address>(
                    from a in contactDto.Addresses
                    select new Address
                        {Country = a.Country, Number = a.Number, Street = a.Street, PostalCode = a.PostalCode, City = a.City}
                ),
                PhoneNumber = contactDto.PhoneNumber
            };
            _context.Contacts.Remove(contact);
            var result = await _context.SaveChangesAsync();
            return (result > 0) ? contactDto : null;
        }
    }
}