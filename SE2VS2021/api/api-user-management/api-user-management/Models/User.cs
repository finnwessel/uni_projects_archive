using System;
using System.ComponentModel.DataAnnotations;
using JsonApiDotNetCore.Resources.Annotations;

namespace api_user_management.Models
{
    public class User
    {
        [Key] public virtual Guid Id { get; set; }

        [Attr(PublicName = "username"), MinLength(1), MaxLength(50)]
        public string Username { get; set; }

        [Attr(PublicName = "avatarId")] public Guid AvatarId { get; set; }

        [Attr(PublicName = "email"), MinLength(3), MaxLength(50)]
        public string Email { get; set; }

        [Attr(PublicName = "firstname"), MinLength(1), MaxLength(50)]
        public string Firstname { get; set; }

        [Attr(PublicName = "lastname"), MinLength(1), MaxLength(50)]
        public string Lastname { get; set; }

        [Attr(PublicName = "password"), MinLength(8), MaxLength(50)]
        public string Password { get; set; }
    }
}