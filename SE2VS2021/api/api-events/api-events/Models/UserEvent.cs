using System.ComponentModel.DataAnnotations;
using JsonApiDotNetCore.Resources.Annotations;

namespace api_events.Models
{
    public class UserEvent
    {
        [Required]
        [Attr(PublicName = "user_id")]
        public Guid? UserId { get; init; }

        [Required]
        [Attr(PublicName = "event_id")]
        public Guid? EventId { get; init; }
        
        [Required]
        [Attr(PublicName = "is_accepted")]
        public bool IsAccepted { get; set; }
    }
}