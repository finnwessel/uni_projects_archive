using System;

namespace api_user_management.Dto
{
    public class AuthResponseDto
    {
        public Guid Id { get; set; }
        public string Firstname { get; set; }
        public string Lastname { get; set; }
        public string Email { get; set; }
        public string Username { get; set; }
        public string Token { get; set; }


        public AuthResponseDto(UserDto user, string token)
        {
            Id = user.Id;
            Firstname = user.Firstname;
            Lastname = user.Lastname;
            Email = user.Email;
            Username = user.Username;
            Token = token;
        }
    }
}