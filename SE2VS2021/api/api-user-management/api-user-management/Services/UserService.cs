using api_user_management.Context;
using api_user_management.Dto;
using api_user_management.Models;
using Microsoft.EntityFrameworkCore;
using RpcCommunication.Objects;

namespace api_user_management.Services
{
    public interface IUserService
    {
        Task<List<UserDto>> GetAll();
        Task<UserDto?> GetById(Guid id);
        Task<UserDetails?> GetUserDetailsById(Guid id);
        UserDto? GetUserWithCredentials(string username, string password);
        Task<List<SearchUser>> GetUsersMatchingQuery(string query);
        Task<UserDto?> UpdateUser(Guid id, UserDto userDto);
        Task<Guid?> CreateUser(NewUserDto userDto);
        Task<UserDto?> DeleteUser(UserDto userDto);
        bool UserExists(Guid id);
    }

    public class UserService : IUserService
    {
        private readonly MariaDbContext _context;

        public UserService(MariaDbContext context)
        {
            _context = context;
        }

        public async Task<List<UserDto>> GetAll()
        {
            return await _context.Users.Select(p => new UserDto
            {
                Id = p.Id, Username = p.Username, Email = p.Email, Firstname = p.Firstname, Lastname = p.Lastname
            }).AsNoTracking().ToListAsync();
        }

        public async Task<UserDto?> GetById(Guid id)
        {
            return await _context.Users.Select(p => new UserDto
            {
                Id = p.Id, Username = p.Username, AvatarId = p.AvatarId, Email = p.Email, Firstname = p.Firstname,
                Lastname = p.Lastname
            }).Where(p => p.Id == id).AsNoTracking().FirstOrDefaultAsync();
        }

        public async Task<UserDetails?> GetUserDetailsById(Guid id)
        {
            return await _context.Users.Select(p => new UserDetails()
            {
                Id = p.Id, AvatarId = p.AvatarId, Email = p.Email, Firstname = p.Firstname,
                Lastname = p.Lastname
            }).Where(p => p.Id == id).AsNoTracking().FirstOrDefaultAsync();
        }

        public async Task<List<SearchUser>> GetUsersMatchingQuery(string query)
        {
            return await _context.Users.Where(m => m.Firstname.Contains(query)
                                                   || m.Lastname.Contains(query)
                                                   || (m.Firstname + " " + m.Lastname).Contains(query)
                                                   || (m.Lastname + " " + m.Firstname).Contains(query)
                                                   || (m.Lastname + ", " + m.Firstname).Contains(query)
            ).Select(p => new SearchUser
            {
                Id = p.Id, Firstname = p.Firstname,
                Lastname = p.Lastname
            }).AsNoTracking().ToListAsync();
        }

        public UserDto? GetUserWithCredentials(string username, string password)
        {
            var user = _context.Users.AsNoTracking()
                .SingleOrDefault(x => x.Username == username && x.Password == password);

            if (user == null)
            {
                return null;
            }

            return new UserDto
            {
                Id = user.Id,
                Username = user.Username,
                AvatarId = user.AvatarId,
                Email = user.Email,
                Firstname = user.Firstname,
                Lastname = user.Lastname
            };
        }

        public async Task<UserDto?> UpdateUser(Guid id, UserDto userDto)
        {
            var user = _context.Users.SingleOrDefault(x => x.Id == id);
            if (user == null)
            {
                return null;
            }

            user.Username = userDto.Username;
            user.AvatarId = userDto.AvatarId;
            user.Email = userDto.Email;
            user.Firstname = userDto.Firstname;
            user.Lastname = userDto.Lastname;

            _context.Entry(user).State = EntityState.Modified;

            var result = await _context.SaveChangesAsync();

            return (result > 0) ? userDto : null;
        }

        public async Task<Guid?> CreateUser(NewUserDto userDto)
        {
            var passwordHashed = SecurityService.Hash(userDto.Password);

            var user = new User
            {
                Id = Guid.NewGuid(),
                Username = userDto.Username,
                Email = userDto.Email,
                Firstname = userDto.Firstname,
                Lastname = userDto.Lastname,
                Password = passwordHashed
            };

            _context.Users.Add(user);
            var result = await _context.SaveChangesAsync();
            if (result > 0)
            {
                return user.Id;
            }
            else
            {
                return null;
            }
        }

        public async Task<UserDto?> DeleteUser(UserDto userDto)
        {
            var user = new User()
            {
                Id = userDto.Id,
                Username = userDto.Username,
                AvatarId = userDto.AvatarId,
                Email = userDto.Email,
                Firstname = userDto.Firstname,
                Lastname = userDto.Lastname
            };
            _context.Users.Remove(user);
            var result = await _context.SaveChangesAsync();
            return (result > 0) ? userDto : null;
        }

        public bool UserExists(Guid id)
        {
            return _context.Users.Any(e => e.Id == id);
        }
    }
}