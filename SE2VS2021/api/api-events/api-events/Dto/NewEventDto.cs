using System;
using System.ComponentModel.DataAnnotations;

namespace api_events.Dto
{
    public class NewEventDto
    {
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