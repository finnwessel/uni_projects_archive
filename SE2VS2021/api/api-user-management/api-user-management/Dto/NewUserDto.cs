using System.ComponentModel.DataAnnotations;

namespace api_user_management.Dto
{
    public class NewUserDto
    {
        [Required,
         DataType(DataType.Text),
         MinLength(1),
         MaxLength(50)]
        public string Username { get; set; }

        [Required,
         DataType(DataType.Text),
         MinLength(3),
         MaxLength(50),
         RegularExpression("^(.+)@(.+)$", ErrorMessage = "Must be valid email format")]
        public string Email { get; set; }

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

        [Required,
         DataType(DataType.Password),
         MinLength(8),
         MaxLength(50)]
        public string Password { get; set; }
    }
}