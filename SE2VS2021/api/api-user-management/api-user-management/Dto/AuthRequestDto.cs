using System.ComponentModel.DataAnnotations;

namespace api_user_management.Dto
{
    public class AuthRequestDto
    {
        [Required, MinLength(1), MaxLength(50)]
        public string Username { get; set; }

        [Required, MinLength(8), MaxLength(50)]
        public string Password { get; set; }
    }
}