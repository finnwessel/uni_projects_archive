using api_events.Context;
using api_events.Dto;
using api_events.Models;
using Microsoft.EntityFrameworkCore;

namespace api_events.Services
{
    public interface IEventService
    {
        Task<Guid?> AddEvent(NewEventDto eventDto, Guid userId);
        Task<EventDto?> DeleteEvent(Event eventDto);
        Task<EventDto?> UpdateEvent(EventDto updatedEvent, Event @event);
        Task<Guid?> AddUserEvent(Guid userId, Guid eventId);
        Task<Guid?> UpdateUserEvent(Guid? userId, Guid eventId);
        Task<Guid?> DeleteUserEvent(Guid? userId, Guid eventId);
        Task<Event?> GetById(Guid? eventId);
        Task<IList<EventDto>> GetAllEvents(Guid userId);
        Task<IList<EventDto>> GetIntervalEvents(Guid userId, EventIntervalDto interval);
        Task<IList<EventDto>> GetInvitedEvents(Guid? userId);
    }

    public class EventService : IEventService
    {
        private readonly MariaDbContext _context;

        public EventService(MariaDbContext context)
        {
            _context = context;
        }

        private static EventDto CreateEventDto(Event e)
        {
            return new EventDto
            {
                Id = e.Id,
                OwnerId = e.OwnerId,
                AllDay = e.AllDay,
                Start = e.StartDate,
                End = e.EndDate,
                Title = e.Title,
                Description = e.Description
            };
        }

        public async Task<Guid?> AddEvent(NewEventDto eventDto, Guid userId)
        {
            Event @event = new()
            {
                OwnerId = userId,
                AllDay = eventDto.AllDay,
                StartDate = eventDto.Start,
                EndDate = eventDto.End,
                Title = eventDto.Title,
                Description = eventDto.Description
            };

            _context.Events.Add(@event);

            UserEvent userEvent = new()
            {
                UserId = userId,
                EventId = @event.Id,
                IsAccepted = true      // must be true in production
            };

            _context.UserEvent.Add(userEvent);

            var result = await _context.SaveChangesAsync();

            return result > 0 ? @event.Id : null;
        }

        public async Task<EventDto?> DeleteEvent(Event @event)
        {
            var userEvents = await
                (from e in _context.Events
                    join ue in _context.UserEvent on e.Id equals ue.EventId
                    where ue.EventId == @event.Id
                    select ue).AsNoTracking().ToListAsync();

            _context.UserEvent.RemoveRange(userEvents);

            _context.Events.Remove(@event);

            var result = await _context.SaveChangesAsync();

            return result > 0 ? CreateEventDto(@event) : null;
        }

        public async Task<EventDto?> UpdateEvent(EventDto updatedEvent, Event originalEvent)
        {
            originalEvent.AllDay = updatedEvent.AllDay;
            originalEvent.StartDate = updatedEvent.Start;
            originalEvent.EndDate = updatedEvent.End;
            originalEvent.Title = updatedEvent.Title;
            originalEvent.Description = updatedEvent.Description;
            
            _context.Events.Update(originalEvent);

            var result = await _context.SaveChangesAsync();

            return result > 0 ? updatedEvent : null;
        }

        public async Task<Guid?> AddUserEvent(Guid userId, Guid eventId)
        {
            UserEvent userEvent = new()
            {
                UserId = userId,
                EventId = eventId,
                IsAccepted = false
            };

            _context.UserEvent.Add(userEvent);
            
            var result = await _context.SaveChangesAsync();

            return result > 0 ? userEvent.EventId : null;
        }

        public async Task<Guid?> UpdateUserEvent(Guid? userId, Guid eventId)
        {
            var userEvent = await _context.UserEvent.FindAsync(userId, eventId);

            if (userEvent == null)
            {
                return null;
            }

            userEvent.IsAccepted = true;

            _context.UserEvent.Update(userEvent);
            
            var result = await _context.SaveChangesAsync();

            return result > 0 ? userEvent.EventId : null;
        }
        
        public async Task<Guid?> DeleteUserEvent(Guid? userId, Guid eventId)
        {
            var userEvent = await _context.UserEvent.FindAsync(userId, eventId);

            if (userEvent == null)
            {
                return null;
            }
            
            _context.UserEvent.Remove(userEvent);
            
            var result = await _context.SaveChangesAsync();

            return result > 0 ? userEvent.EventId : null;
        }

        public async Task<Event?> GetById(Guid? eventId)
        {
            return await _context.Events.Where(e => e.Id == eventId).AsNoTracking().FirstOrDefaultAsync();
        }

        public async Task<IList<EventDto>> GetAllEvents(Guid userId)
        {
            return await
                (from e in _context.Events
                    join ue in _context.UserEvent on e.Id equals ue.EventId
                    where ue.UserId == userId && ue.IsAccepted  == true
                    select CreateEventDto(e)
                ).AsNoTracking().ToListAsync();
        }

        public async Task<IList<EventDto>> GetIntervalEvents(Guid userId, EventIntervalDto interval)
        {
            return await
                (from e in _context.Events
                    where interval.From <= e.StartDate && interval.To >= e.StartDate
                    join ue in _context.UserEvent on e.Id equals ue.EventId
                    where ue.UserId == userId && ue.IsAccepted == true
                    select CreateEventDto(e)
                ).AsNoTracking().ToListAsync();
        }

        public async Task<IList<EventDto>> GetInvitedEvents(Guid? userId)
        {
            return await
                (from e in _context.Events
                    join ue in _context.UserEvent on e.Id equals ue.EventId
                    where ue.UserId == userId && ue.IsAccepted == false
                    select CreateEventDto(e)
                ).AsNoTracking().ToListAsync();
        }
    }
}