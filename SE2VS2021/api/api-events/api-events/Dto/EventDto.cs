using System;
using System.ComponentModel.DataAnnotations;

namespace api_events.Dto
{
    public class EventDto
    {
        [Required]
        public Guid? Id { get; set; }
        
        public Guid? OwnerId { get; set; }
        
        [Required]
        public bool AllDay { get; set; }
        
        [Required]
        public DateTime Start { get; set; }
        
        public DateTime End { get; set; }
        
        [Required]
        public string? Title { get; set; }
        
        public string? Description { get; set; }
    }
}