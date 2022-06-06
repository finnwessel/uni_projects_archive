using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using JsonApiDotNetCore.Resources.Annotations;

namespace api_events.Models
{
    public class Event
    {
        [Key] 
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        public Guid? Id { get; init; }

        [Required]
        [Attr(PublicName = "owner")]
        public Guid? OwnerId { get; init; }
        
        [Required]
        [Attr(PublicName = "all_day")]
        public bool AllDay { get; set; }
        
        [Required]
        [Attr(PublicName = "start")]
        public DateTime StartDate { get; set; }
        
        [Attr(PublicName = "end")]
        public DateTime EndDate { get; set; }
        
        [Required]
        [Attr(PublicName = "title")]
        public string? Title { get; set; }

        [Attr(PublicName = "description")] 
        public string? Description { get; set; }
    }
    
}