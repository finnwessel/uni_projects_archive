using System.ComponentModel.DataAnnotations;

namespace api_events.Dto
{
    public class EventIntervalDto
    {
        public DateTime? From { get; set; }

        public DateTime? To { get; set; }
    }
}
