using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using api_events.Controllers;
using api_events.Dto;
using api_events.Models;
using api_events.Services;
using FakeItEasy;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using UserNotification;
using VersionLogging;
using Xunit;

namespace api_events_test.Controller;

public class EventControllerTest
{
    // AddEvent
    [Fact]
    public async void AddEvent_Returns_Guid_Of_Event()
    {
        var userId = Guid.NewGuid();
        var fakeEvent = new NewEventDto
        {
            Start = DateTime.Now,
            End = DateTime.Now,
            Title = "Title",
            Description = "Description",
            AllDay = true,
        };
        Guid? guid = Guid.NewGuid();

        var eventService = A.Fake<IEventService>();
        var notificationPublisher = A.Fake<INotificationPublisher>();
        var versionLogPublisher = A.Fake<IVersionLogPublisher>();
        A.CallTo(() => eventService.AddEvent(fakeEvent, userId)).Returns(Task.FromResult(guid));
        A.CallTo(() => notificationPublisher.Send(A.Dummy<Guid>(), A.Dummy<string>(),A.Dummy<string>())).DoesNothing();
        EventController eventController = new(eventService, notificationPublisher, versionLogPublisher)
        {
            ControllerContext = BindUserToContext(userId)
        };
            

        // Act
        var actionResult = await eventController.AddEvent(fakeEvent);

        // Assert
        var result = actionResult as OkObjectResult;
        Assert.IsType<Guid>(result?.Value);
    }
    
    [Fact]
    public async void AddEvent_Without_UserId_Returns_BadRequest()
    {
        var fakeEvent = new NewEventDto
        {
            Start = DateTime.Now,
            End = DateTime.Now,
            Title = "Title",
            Description = "Description",
            AllDay = true,
        };

        var eventService = A.Fake<IEventService>();
        var notificationPublisher = A.Fake<INotificationPublisher>();
        var versionLogPublisher = A.Fake<IVersionLogPublisher>();
        A.CallTo(() => notificationPublisher.Send(A.Dummy<Guid>(), A.Dummy<string>(),A.Dummy<string>())).DoesNothing();
        EventController eventController = new(eventService, notificationPublisher, versionLogPublisher)
        {
            ControllerContext = BindUserToContext(null)
        };
            

        // Act
        var actionResult = await eventController.AddEvent(fakeEvent);

        // Assert
        Assert.IsType<BadRequestObjectResult>(actionResult);
    }
    
    [Fact]
    public async void AddEvent_Unable_To_Add_Returns_BadRequest()
    {
        var userId = Guid.NewGuid();
        var fakeEvent = A.Dummy<NewEventDto>();

        var eventService = A.Fake<IEventService>();
        var notificationPublisher = A.Fake<INotificationPublisher>();
        var versionLogPublisher = A.Fake<IVersionLogPublisher>();
        A.CallTo(() => eventService.AddEvent(fakeEvent, userId)).Returns(Task.FromResult<Guid?>(null));
        A.CallTo(() => notificationPublisher.Send(A.Dummy<Guid>(), A.Dummy<string>(),A.Dummy<string>())).DoesNothing();
        EventController eventController = new(eventService, notificationPublisher, versionLogPublisher)
        {
            ControllerContext = BindUserToContext(userId)
        };
            

        // Act
        var actionResult = await eventController.AddEvent(fakeEvent);

        // Assert
        Assert.IsType<BadRequestObjectResult>(actionResult);
    }
    
    // DeleteEvent
    [Fact]
    public async void DeleteEvent_Returns_Deleted_Event()
    {
        var userId = Guid.NewGuid();
        var eventId = Guid.NewGuid();

        var eventService = A.Fake<IEventService>();
        var notificationPublisher = A.Fake<INotificationPublisher>();
        var versionLogPublisher = A.Fake<IVersionLogPublisher>();
        var @event = new Event
        {
            Id = eventId,
            OwnerId = userId
        };
        var eventDto = new EventDto
        {
            Id = eventId,
            OwnerId = userId
        };
        
        A.CallTo(() => eventService.GetById(eventId)).Returns(Task.FromResult<Event?>(@event));
        A.CallTo(() => eventService.DeleteEvent(@event)).Returns(Task.FromResult<EventDto?>(eventDto));
        
        EventController eventController = new(eventService, notificationPublisher, versionLogPublisher)
        {
            ControllerContext = BindUserToContext(userId)
        };
            

        // Act
        var actionResult = await eventController.DeleteEvent(eventId);

        // Assert
        var result = actionResult as OkObjectResult;
        Assert.IsType<EventDto>(result?.Value);
    }
    
    [Fact]
    public async void DeleteEvent_Of_Not_Existing_Event_Returns_BadRequest()
    {
        var userId = Guid.NewGuid();
        var eventId = Guid.NewGuid();

        var eventService = A.Fake<IEventService>();
        var notificationPublisher = A.Fake<INotificationPublisher>();
        var versionLogPublisher = A.Fake<IVersionLogPublisher>();
        var @event = new Event
        {
            Id = eventId,
            OwnerId = userId
        };
        var eventDto = new EventDto
        {
            Id = eventId,
            OwnerId = userId
        };
        
        A.CallTo(() => eventService.GetById(eventId)).Returns(Task.FromResult<Event?>(null));
        A.CallTo(() => eventService.DeleteEvent(@event)).Returns(Task.FromResult<EventDto?>(eventDto));
        
        EventController eventController = new(eventService, notificationPublisher, versionLogPublisher)
        {
            ControllerContext = BindUserToContext(userId)
        };
            

        // Act
        var actionResult = await eventController.DeleteEvent(eventId);

        // Assert
        Assert.IsType<BadRequestObjectResult>(actionResult);
    }
    
    [Fact]
    public async void DeleteEvent_For_Not_Owned_Event_Returns_BadRequest()
    {
        var userId = Guid.NewGuid();
        var eventId = Guid.NewGuid();
        var ownerId = Guid.NewGuid();
        
        var eventService = A.Fake<IEventService>();
        var notificationPublisher = A.Fake<INotificationPublisher>();
        var versionLogPublisher = A.Fake<IVersionLogPublisher>();
        var @event = new Event
        {
            Id = eventId,
            OwnerId = ownerId
        };
        var eventDto = new EventDto
        {
            Id = eventId,
            OwnerId = ownerId
        };
        
        A.CallTo(() => eventService.GetById(eventId)).Returns(Task.FromResult<Event?>(@event));
        A.CallTo(() => eventService.DeleteEvent(@event)).Returns(Task.FromResult<EventDto?>(eventDto));
        
        EventController eventController = new(eventService, notificationPublisher, versionLogPublisher)
        {
            ControllerContext = BindUserToContext(userId)
        };
            

        // Act
        var actionResult = await eventController.DeleteEvent(eventId);

        // Assert
        Assert.IsType<BadRequestObjectResult>(actionResult);
    }
    
    // UpdateEvent
    [Fact]
    public async void UpdateEvent_Returns_Updated_Event()
    {
        // Arrange
        var userId = Guid.NewGuid();
        var eventId = Guid.NewGuid();

        var eventService = A.Fake<IEventService>();
        var notificationPublisher = A.Fake<INotificationPublisher>();
        var versionLogPublisher = A.Fake<IVersionLogPublisher>();
        var @event = new Event
        {
            Id = eventId,
            OwnerId = userId
        };
        var eventDto = new EventDto
        {
            Id = eventId,
            OwnerId = userId
        };
        
        A.CallTo(() => eventService.GetById(eventId)).Returns(Task.FromResult<Event?>(@event));
        A.CallTo(() => eventService.UpdateEvent(eventDto, @event)).Returns(Task.FromResult<EventDto?>(eventDto));
        
        EventController eventController = new(eventService, notificationPublisher, versionLogPublisher)
        {
            ControllerContext = BindUserToContext(userId)
        };
            

        // Act
        var actionResult = await eventController.UpdateEvent(eventDto);

        // Assert
        var result = actionResult as OkObjectResult;
        Assert.IsType<EventDto>(result?.Value);
    }
    
    [Fact]
    public async void UpdateEvent_For_Not_Existing_Event_Returns_NotFound()
    {
        // Arrange
        var userId = Guid.NewGuid();
        var eventId = Guid.NewGuid();

        var eventService = A.Fake<IEventService>();
        var notificationPublisher = A.Fake<INotificationPublisher>();
        var versionLogPublisher = A.Fake<IVersionLogPublisher>();
        var @event = new Event
        {
            Id = eventId,
            OwnerId = userId
        };
        var eventDto = new EventDto
        {
            Id = eventId,
            OwnerId = userId
        };
        
        A.CallTo(() => eventService.GetById(eventId)).Returns(Task.FromResult<Event?>(null));
        A.CallTo(() => eventService.UpdateEvent(eventDto, @event)).Returns(Task.FromResult<EventDto?>(eventDto));
        
        EventController eventController = new(eventService, notificationPublisher, versionLogPublisher)
        {
            ControllerContext = BindUserToContext(userId)
        };
            

        // Act
        var actionResult = await eventController.UpdateEvent(eventDto);

        // Assert
        Assert.IsType<NotFoundObjectResult>(actionResult);
    }
    
    [Fact]
    public async void UpdateEvent_For_Not_Owned_Event_Returns_BadRequest()
    {
        // Arrange
        var userId = Guid.NewGuid();
        var eventId = Guid.NewGuid();
        var ownerId = Guid.NewGuid();

        var eventService = A.Fake<IEventService>();
        var notificationPublisher = A.Fake<INotificationPublisher>();
        var versionLogPublisher = A.Fake<IVersionLogPublisher>();
        var @event = new Event
        {
            Id = eventId,
            OwnerId = ownerId
        };
        var eventDto = new EventDto
        {
            Id = eventId,
            OwnerId = ownerId
        };
        
        A.CallTo(() => eventService.GetById(eventId)).Returns(Task.FromResult<Event?>(@event));
        A.CallTo(() => eventService.UpdateEvent(eventDto, @event)).Returns(Task.FromResult<EventDto?>(eventDto));
        
        EventController eventController = new(eventService, notificationPublisher, versionLogPublisher)
        {
            ControllerContext = BindUserToContext(userId)
        };
            

        // Act
        var actionResult = await eventController.UpdateEvent(eventDto);

        // Assert
        Assert.IsType<BadRequestObjectResult>(actionResult);
    }
    
    [Fact]
    public async void UpdateEvent_Unable_To_Update_Returns_BadRequest()
    {
        // Arrange
        var userId = Guid.NewGuid();
        var eventId = Guid.NewGuid();

        var eventService = A.Fake<IEventService>();
        var notificationPublisher = A.Fake<INotificationPublisher>();
        var versionLogPublisher = A.Fake<IVersionLogPublisher>();
        var @event = new Event
        {
            Id = eventId,
            OwnerId = userId
        };
        var eventDto = new EventDto
        {
            Id = eventId,
            OwnerId = userId
        };
        
        A.CallTo(() => eventService.GetById(eventId)).Returns(Task.FromResult<Event?>(@event));
        A.CallTo(() => eventService.UpdateEvent(eventDto, @event)).Returns(Task.FromResult<EventDto?>(null));
        
        EventController eventController = new(eventService, notificationPublisher, versionLogPublisher)
        {
            ControllerContext = BindUserToContext(userId)
        };
            

        // Act
        var actionResult = await eventController.UpdateEvent(eventDto);

        // Assert
        Assert.IsType<BadRequestObjectResult>(actionResult);
    }
    
    // AddUserEventInvite
    [Fact]
    public async void AddUserEventInvite_Returns_Guid_Of_Event()
    {
        // Arrange
        var userId = Guid.NewGuid();
        var contactId = Guid.NewGuid();
        var eventId = Guid.NewGuid();

        var eventService = A.Fake<IEventService>();
        var notificationPublisher = A.Fake<INotificationPublisher>();
        var versionLogPublisher = A.Fake<IVersionLogPublisher>();
        var @event = new Event
        {
            Id = eventId,
            OwnerId = userId
        };

        A.CallTo(() => eventService.GetById(eventId)).Returns(Task.FromResult<Event?>(@event));
        A.CallTo(() => eventService.AddUserEvent(contactId, eventId)).Returns(Task.FromResult<Guid?>(eventId));
        A.CallTo(() => notificationPublisher.Send(A.Dummy<Guid>(), A.Dummy<string>(),A.Dummy<string>())).DoesNothing();
        
        EventController eventController = new(eventService, notificationPublisher, versionLogPublisher)
        {
            ControllerContext = BindUserToContext(userId)
        };
            

        // Act
        var actionResult = await eventController.AddUserEvent(contactId, eventId);

        // Assert
        var result = actionResult as OkObjectResult;
        Assert.IsType<Guid>(result?.Value);
    }
    
    [Fact]
    public async void AddUserEventInvite_For_Not_Existing_Event_Returns_NotFound()
    {
        // Arrange
        var userId = Guid.NewGuid();
        var contactId = Guid.NewGuid();
        var eventId = Guid.NewGuid();

        var eventService = A.Fake<IEventService>();
        var notificationPublisher = A.Fake<INotificationPublisher>();
        var versionLogPublisher = A.Fake<IVersionLogPublisher>();
        var @event = new Event
        {
            Id = eventId,
            OwnerId = userId
        };

        A.CallTo(() => eventService.GetById(eventId)).Returns(Task.FromResult<Event?>(null));
        A.CallTo(() => eventService.AddUserEvent(contactId, eventId)).Returns(Task.FromResult<Guid?>(eventId));
        A.CallTo(() => notificationPublisher.Send(A.Dummy<Guid>(), A.Dummy<string>(),A.Dummy<string>())).DoesNothing();
        
        EventController eventController = new(eventService, notificationPublisher, versionLogPublisher)
        {
            ControllerContext = BindUserToContext(userId)
        };
            

        // Act
        var actionResult = await eventController.AddUserEvent(contactId, eventId);

        // Assert
        Assert.IsType<NotFoundObjectResult>(actionResult);
    }
    
    [Fact]
    public async void AddUserEventInvite_For_Not_Owned_Event_Returns_BadRequest()
    {
        // Arrange
        var userId = Guid.NewGuid();
        var contactId = Guid.NewGuid();
        var eventId = Guid.NewGuid();
        var ownerId = Guid.NewGuid();

        var eventService = A.Fake<IEventService>();
        var notificationPublisher = A.Fake<INotificationPublisher>();
        var versionLogPublisher = A.Fake<IVersionLogPublisher>();
        var @event = new Event
        {
            Id = eventId,
            OwnerId = ownerId
        };

        A.CallTo(() => eventService.GetById(eventId)).Returns(Task.FromResult<Event?>(@event));
        A.CallTo(() => eventService.AddUserEvent(contactId, eventId)).Returns(Task.FromResult<Guid?>(eventId));
        A.CallTo(() => notificationPublisher.Send(A.Dummy<Guid>(), A.Dummy<string>(),A.Dummy<string>())).DoesNothing();
        
        EventController eventController = new(eventService, notificationPublisher, versionLogPublisher)
        {
            ControllerContext = BindUserToContext(userId)
        };
            

        // Act
        var actionResult = await eventController.AddUserEvent(contactId, eventId);

        // Assert
        Assert.IsType<BadRequestObjectResult>(actionResult);
    }
    
    // UpdateUserEventInvite
    [Fact]
    public async void UpdateUserEventInvite_Returns_Guid_Of_Event()
    {
        // Arrange
        var userId = Guid.NewGuid();
        var eventId = Guid.NewGuid();

        var eventService = A.Fake<IEventService>();
        var notificationPublisher = A.Fake<INotificationPublisher>();
        var versionLogPublisher = A.Fake<IVersionLogPublisher>();
        
        A.CallTo(() => eventService.UpdateUserEvent(userId, eventId)).Returns(Task.FromResult<Guid?>(eventId));

        EventController eventController = new(eventService, notificationPublisher, versionLogPublisher)
        {
            ControllerContext = BindUserToContext(userId)
        };
        
        // Act
        var actionResult = await eventController.UpdateUserEvent(eventId);

        // Assert
        var result = actionResult as OkObjectResult;
        Assert.IsType<Guid>(result?.Value);
    }
    
    [Fact]
    public async void UpdateUserEventInvite_Unable_To_Update_Returns_BadRequest()
    {
        // Arrange
        var userId = Guid.NewGuid();
        var eventId = Guid.NewGuid();

        var eventService = A.Fake<IEventService>();
        var notificationPublisher = A.Fake<INotificationPublisher>();
        var versionLogPublisher = A.Fake<IVersionLogPublisher>();
        
        A.CallTo(() => eventService.UpdateUserEvent(userId, eventId)).Returns(Task.FromResult<Guid?>(null));

        EventController eventController = new(eventService, notificationPublisher, versionLogPublisher)
        {
            ControllerContext = BindUserToContext(userId)
        };
        
        // Act
        var actionResult = await eventController.UpdateUserEvent(eventId);

        // Assert
        Assert.IsType<BadRequestObjectResult>(actionResult);
    }
    
    // DeleteUserEventInvite
    [Fact]
    public async void DeleteUserEventInvite_Returns_Guid_Of_Event()
    {
        // Arrange
        var userId = Guid.NewGuid();
        var ownerId = Guid.NewGuid();
        var eventId = Guid.NewGuid();

        var eventService = A.Fake<IEventService>();
        var notificationPublisher = A.Fake<INotificationPublisher>();
        var versionLogPublisher = A.Fake<IVersionLogPublisher>();
        var @event = new Event
        {
            Id = eventId,
            OwnerId = ownerId
        };
        
        A.CallTo(() => eventService.GetById(eventId)).Returns(Task.FromResult<Event?>(@event));
        A.CallTo(() => eventService.DeleteUserEvent(userId, eventId)).Returns(Task.FromResult<Guid?>(eventId));

        EventController eventController = new(eventService, notificationPublisher, versionLogPublisher)
        {
            ControllerContext = BindUserToContext(userId)
        };
        
        // Act
        var actionResult = await eventController.DeleteUserEvent(eventId);

        // Assert
        var result = actionResult as OkObjectResult;
        Assert.IsType<Guid>(result?.Value);
    }
    
    [Fact]
    public async void DeleteUserEventInvite_For_Not_Existing_Event_Returns_NotFound()
    {
        // Arrange
        var userId = Guid.NewGuid();
        var eventId = Guid.NewGuid();

        var eventService = A.Fake<IEventService>();
        var notificationPublisher = A.Fake<INotificationPublisher>();
        var versionLogPublisher = A.Fake<IVersionLogPublisher>();
        
        A.CallTo(() => eventService.GetById(eventId)).Returns(Task.FromResult<Event?>(null));
        A.CallTo(() => eventService.DeleteUserEvent(userId, eventId)).Returns(Task.FromResult<Guid?>(eventId));

        EventController eventController = new(eventService, notificationPublisher, versionLogPublisher)
        {
            ControllerContext = BindUserToContext(userId)
        };
        
        // Act
        var actionResult = await eventController.DeleteUserEvent(eventId);

        // Assert
        Assert.IsType<NotFoundObjectResult>(actionResult);
    }
    
    [Fact]
    public async void DeleteUserEventInvite_For_Owned_Event_Returns_BadRequest()
    {
        // Arrange
        var userId = Guid.NewGuid();
        var eventId = Guid.NewGuid();

        var eventService = A.Fake<IEventService>();
        var notificationPublisher = A.Fake<INotificationPublisher>();
        var versionLogPublisher = A.Fake<IVersionLogPublisher>();
        var @event = new Event
        {
            Id = eventId,
            OwnerId = userId
        };
        
        A.CallTo(() => eventService.GetById(eventId)).Returns(Task.FromResult<Event?>(@event));
        A.CallTo(() => eventService.DeleteUserEvent(userId, eventId)).Returns(Task.FromResult<Guid?>(eventId));

        EventController eventController = new(eventService, notificationPublisher, versionLogPublisher)
        {
            ControllerContext = BindUserToContext(userId)
        };
        
        // Act
        var actionResult = await eventController.DeleteUserEvent(eventId);

        // Assert
        Assert.IsType<BadRequestObjectResult>(actionResult);
    }
    
    [Fact]
    public async void DeleteUserEventInvite_Unable_To_Delete_Returns_BadRequest()
    {
        // Arrange
        var userId = Guid.NewGuid();
        var ownerId = Guid.NewGuid();
        var eventId = Guid.NewGuid();

        var eventService = A.Fake<IEventService>();
        var notificationPublisher = A.Fake<INotificationPublisher>();
        var versionLogPublisher = A.Fake<IVersionLogPublisher>();
        var @event = new Event
        {
            Id = eventId,
            OwnerId = ownerId
        };
        
        A.CallTo(() => eventService.GetById(eventId)).Returns(Task.FromResult<Event?>(@event));
        A.CallTo(() => eventService.DeleteUserEvent(userId, eventId)).Returns(Task.FromResult<Guid?>(null));

        EventController eventController = new(eventService, notificationPublisher, versionLogPublisher)
        {
            ControllerContext = BindUserToContext(userId)
        };
        
        // Act
        var actionResult = await eventController.DeleteUserEvent(eventId);

        // Assert
        Assert.IsType<BadRequestObjectResult>(actionResult);
    }
    
    // GetEvents
    [Fact]
    public async void GetEvents_With_From_Returns_List_Of_Events()
    {
        // Arrange
        var userId = Guid.NewGuid();
        var intervalDto = new EventIntervalDto
        {
            From = A.Dummy<DateTime>(),
            To = null
        };
        var eventDtoList = A.CollectionOfDummy<EventDto>(5);

        var eventService = A.Fake<IEventService>();
        var notificationPublisher = A.Fake<INotificationPublisher>();
        var versionLogPublisher = A.Fake<IVersionLogPublisher>();
        
        A.CallTo(() => eventService.GetIntervalEvents(userId, intervalDto)).Returns(Task.FromResult(eventDtoList));
        A.CallTo(() => eventService.GetAllEvents(userId)).Returns(Task.FromResult(eventDtoList));

        EventController eventController = new(eventService, notificationPublisher, versionLogPublisher)
        {
            ControllerContext = BindUserToContext(userId)
        };
        
        // Act
        var actionResult = await eventController.GetEvents(intervalDto);

        // Assert
        var result = actionResult as OkObjectResult;
        var events = result?.Value as IList<EventDto>;
        Assert.NotNull(events);
    }
    
    [Fact]
    public async void GetEvents_With_Interval_Returns_List_Of_Events()
    {
        // Arrange
        var userId = Guid.NewGuid();
        var intervalDto = new EventIntervalDto
        {
            From = A.Dummy<DateTime>(),
            To = A.Dummy<DateTime>()
        };
        var eventDtoList = A.CollectionOfDummy<EventDto>(5);

        var eventService = A.Fake<IEventService>();
        var notificationPublisher = A.Fake<INotificationPublisher>();
        var versionLogPublisher = A.Fake<IVersionLogPublisher>();
        
        A.CallTo(() => eventService.GetIntervalEvents(userId, intervalDto)).Returns(Task.FromResult(eventDtoList));
        A.CallTo(() => eventService.GetAllEvents(userId)).Returns(Task.FromResult(eventDtoList));

        EventController eventController = new(eventService, notificationPublisher, versionLogPublisher)
        {
            ControllerContext = BindUserToContext(userId)
        };
        
        // Act
        var actionResult = await eventController.GetEvents(intervalDto);

        // Assert
        var result = actionResult as OkObjectResult;
        var events = result?.Value as IList<EventDto>;
        Assert.NotNull(events);
    }
    
    [Fact]
    public async void GetEvents_Without_Interval_Returns_List_Of_Events()
    {
        // Arrange
        var userId = Guid.NewGuid();
        var intervalDto = new EventIntervalDto
        {
            From = null,
            To = null
        };
        var eventDtoList = A.CollectionOfDummy<EventDto>(5);

        var eventService = A.Fake<IEventService>();
        var notificationPublisher = A.Fake<INotificationPublisher>();
        var versionLogPublisher = A.Fake<IVersionLogPublisher>();
        
        A.CallTo(() => eventService.GetIntervalEvents(userId, intervalDto)).Returns(Task.FromResult(eventDtoList));
        A.CallTo(() => eventService.GetAllEvents(userId)).Returns(Task.FromResult(eventDtoList));

        EventController eventController = new(eventService, notificationPublisher, versionLogPublisher)
        {
            ControllerContext = BindUserToContext(userId)
        };
        
        // Act
        var actionResult = await eventController.GetEvents(intervalDto);

        // Assert
        var result = actionResult as OkObjectResult;
        var events = result?.Value as IList<EventDto>;
        Assert.NotNull(events);
    }
    
    [Fact]
    public async void GetEvents_Without_UserId_Returns_BadRequest()
    {
        // Arrange
        var userId = Guid.NewGuid();
        var intervalDto = new EventIntervalDto
        {
            From = A.Dummy<DateTime>(),
            To = A.Dummy<DateTime>()
        };
        var eventDtoList = A.CollectionOfDummy<EventDto>(5);

        var eventService = A.Fake<IEventService>();
        var notificationPublisher = A.Fake<INotificationPublisher>();
        var versionLogPublisher = A.Fake<IVersionLogPublisher>();
        
        A.CallTo(() => eventService.GetIntervalEvents(userId, intervalDto)).Returns(Task.FromResult(eventDtoList));
        A.CallTo(() => eventService.GetAllEvents(userId)).Returns(Task.FromResult(eventDtoList));

        EventController eventController = new(eventService, notificationPublisher, versionLogPublisher)
        {
            ControllerContext = BindUserToContext(null)
        };
        
        // Act
        var actionResult = await eventController.GetEvents(intervalDto);

        // Assert
        Assert.IsType<BadRequestObjectResult>(actionResult);
    }
    
    // GetInvitedEvents
    [Fact]
    public async void GetInvitedEvents_Returns_List_Of_Events()
    {
        // Arrange
        var userId = Guid.NewGuid();
        var eventDtoList = A.CollectionOfDummy<EventDto>(5);

        var eventService = A.Fake<IEventService>();
        var notificationPublisher = A.Fake<INotificationPublisher>();
        var versionLogPublisher = A.Fake<IVersionLogPublisher>();
        
        A.CallTo(() => eventService.GetInvitedEvents(userId)).Returns(Task.FromResult(eventDtoList));

        EventController eventController = new(eventService, notificationPublisher, versionLogPublisher)
        {
            ControllerContext = BindUserToContext(userId)
        };
        
        // Act
        var actionResult = await eventController.GetInvitedEvents();

        // Assert
        var result = actionResult as OkObjectResult;
        var events = result?.Value as IList<EventDto>;
        Assert.NotNull(events);
        Assert.Equal(events?.Count, eventDtoList.Count);
    }
    
    // Methods takes a Guid and binds it to HttpContext
    private ControllerContext BindUserToContext(Guid? userId)
    {
        return new ControllerContext
        {
            HttpContext = new DefaultHttpContext
            {
                Items = new Dictionary<object, object?> {{"AuthUserId", userId}}
            }
        };
    }
}