using api_events.Dto;
using api_events.Services;
using JwtMiddleware.Helpers;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Diagnostics.HealthChecks;
using Microsoft.Extensions.Options;
using UserNotification;
using VersionLogging;

namespace api_events.Controllers
{
    [ApiController]
    [Route("[controller]")]

    public class EventController : Controller
    {
        private readonly IEventService _eventService;
        private readonly INotificationPublisher _notificationPublisher;
        private readonly IVersionLogPublisher _versionLogPublisher;
        

        public EventController(IEventService eventService, INotificationPublisher notificationPublisher, IVersionLogPublisher versionLogPublisher)
        {
            _eventService = eventService;
            _versionLogPublisher = versionLogPublisher;
            _notificationPublisher = notificationPublisher;
        }
        
        [Auth]
        [HttpPut]
        public async Task<IActionResult> AddEvent([FromBody] NewEventDto eventDto)
        {
            var userId = (Guid?) HttpContext.Items["AuthUserId"];
            if (userId == null)
            {
                return BadRequest("No valid user ID in request");
            }
            
            var @event = await _eventService.AddEvent(eventDto, userId.Value);
            if (@event == null)
            {
                return BadRequest("Unable to add event");
            }
            
            _versionLogPublisher.Log(
                @event.Value,
                "Created event.",
                "event",
                userId);

            return Ok(@event);
        }

        [Auth]
        [HttpDelete]
        public async Task<IActionResult> DeleteEvent([FromQuery] Guid eventId)
        {
            var userId = (Guid?) HttpContext.Items["AuthUserId"];

            var @event = await _eventService.GetById(eventId);

            if (@event == null)
            {
                return BadRequest("Requested event already doesn't exist.");
            }
            
            if (userId != @event.OwnerId)
            {
                return BadRequest("Only the owner is allowed to delete the requested event.");
            }
            
            var deletedEvent = await _eventService.DeleteEvent(@event);

            if (deletedEvent?.Id == null)
            {
                return BadRequest("Could not delete event.");
            }
            
            _versionLogPublisher.Log(
                deletedEvent.Id.Value,
                "Deleted event.",
                "event",
                userId);
            
            return Ok(deletedEvent);
        }

        [Auth]
        [HttpPost]
        public async Task<IActionResult> UpdateEvent([FromBody] EventDto updatedEvent)
        {
            var userId = (Guid?) HttpContext.Items["AuthUserId"];

            var @event = await _eventService.GetById(updatedEvent.Id);

            if (@event == null)
            {
                return NotFound("Requested event doesn't exist.");
            }
            
            if (userId != @event.OwnerId)
            {
                return BadRequest("Only the owner is allowed to update the requested event.");
            }

            var resultEvent = await _eventService.UpdateEvent(updatedEvent, @event);
            
            if (resultEvent?.Id == null)
            {
                return BadRequest("Unable to update the requested event");
            }
            
            _versionLogPublisher.Log(
                resultEvent.Id.Value,
                "Updated event.",
                "event",
                userId);

            return Ok(resultEvent);
        }

        [Auth]
        [HttpPut("invites")]
        public async Task<IActionResult> AddUserEvent([FromQuery] Guid contactId, Guid eventId)
        {
            var userId = (Guid?) HttpContext.Items["AuthUserId"];

            var @event = await _eventService.GetById(eventId);

            if (@event == null)
            {
                return NotFound("Event does not exist.");
            }

            if (@event.OwnerId != userId)
            {
                return BadRequest("Only the owner ot the event can add a user.");
            }

            var id = await _eventService.AddUserEvent(contactId, eventId);
            
            _notificationPublisher.Send(contactId, "EVENT:INVITE",
                $"You've been invited to {@event.Title}");

            return Ok(id);
        }

        [Auth]
        [HttpPost("invites")]
        public async Task<IActionResult> UpdateUserEvent([FromQuery] Guid eventId)
        {
            var userId = (Guid?) HttpContext.Items["AuthUserId"];

            var id = await _eventService.UpdateUserEvent(userId, eventId);
            
            if (id == null)
            {
                return BadRequest("Unable to update event connection.");
            }

            var @event = await _eventService.GetById(eventId);

            if (@event != null && @event.OwnerId != null)
            {
                _notificationPublisher.Send(@event.OwnerId.Value, "EVENT:INVITE",
                    $"Your invitation was accepted");
            }

            return Ok(id);
        }
        
        [Auth]
        [HttpDelete("invites")]
        public async Task<IActionResult> DeleteUserEvent([FromQuery] Guid eventId)
        {
            var userId = (Guid?) HttpContext.Items["AuthUserId"];

            var @event = await _eventService.GetById(eventId);

            if (@event == null)
            {
                return NotFound("Event with passed ID does not exist.");
            }

            if (@event.OwnerId == userId)
            {
                return BadRequest("This relation cannot be deleted. Delete the event first");
            }

            var id = await _eventService.DeleteUserEvent(userId, eventId);

            if (id == null)
            {
                return BadRequest("Unable to delete event connection.");
            }

            return Ok(id);
        }

        [Auth]
        [HttpGet]
        public async Task<IActionResult> GetEvents([FromQuery] EventIntervalDto interval)
        {
            var userId = (Guid?) HttpContext.Items["AuthUserId"];

            if (userId == null)
            {
                return BadRequest("No valid user ID in request");
            }

            IList<EventDto> events;
            switch (true)
            {
                case true when interval.From != null && interval.To == null:
                    interval.To = new DateTime(3000, 1, 1);
                    events = await _eventService.GetIntervalEvents(userId.Value, interval);
                    break;
                case true when interval.From == null && interval.To == null:
                    events = await _eventService.GetAllEvents(userId.Value);
                    break;
                default:
                    events = await _eventService.GetIntervalEvents(userId.Value, interval);
                    break;
            }
            return Ok(events);
        }

        [Auth]
        [HttpGet("invites")]
        public async Task<IActionResult> GetInvitedEvents()
        {
            var userId = (Guid?) HttpContext.Items["AuthUserId"];

            var events = await _eventService.GetInvitedEvents(userId);

            return Ok(events);
        }
    }
}