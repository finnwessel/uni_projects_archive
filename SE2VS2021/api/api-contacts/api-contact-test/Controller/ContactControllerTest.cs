using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using api_contact.Controllers;
using api_contact.Dto;
using api_contact.Services;
using FakeItEasy;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using RpcCommunication;
using Xunit;

namespace api_contact_test.Controller;

public class ContactControllerTest
{
    [Fact]
    public async void GetAll_Returns_AllContacts()
    {
        // Arrange
        var userId = Guid.NewGuid();
        const int contactCount = 5;
        var fakeContacts = A.CollectionOfDummy<ContactDto>(contactCount).ToList();
        var rpcClient = A.Fake<IRpcClient>();
        var contactService = A.Fake<IContactService>();
        A.CallTo( () => contactService.GetAll(userId)).Returns(Task.FromResult(fakeContacts));
        ContactController contactController = new(contactService, rpcClient)
        {
            ControllerContext = BindUserToContext(userId)
        };
        
        // Act
        var actionResult = await contactController.GetAll();
        
        // Assert
        var result = actionResult.Result as OkObjectResult;
        var returnContacts = result?.Value as IEnumerable<ContactDto>;
        Assert.IsType<OkObjectResult>(actionResult.Result as OkObjectResult);
        Assert.Equal(contactCount, returnContacts?.Count());
    }

    [Fact]
    public async void GetContact_Returns_Correct_Contact()
    {
        // Arrange
        var contactId = Guid.NewGuid();
        var userId = Guid.NewGuid();
        var fakeContact = new ContactDto
        {
            Id = contactId
        };
        var contactService = A.Fake<IContactService>();
        var rpcClient = A.Fake<IRpcClient>();
        A.CallTo(() => contactService.GetById(contactId, userId)).Returns(fakeContact);
        ContactController contactController = new(contactService, rpcClient) 
        {
            ControllerContext = BindUserToContext(userId)
        };
        
        // Act
        var actionResult = await contactController.GetContact(contactId);
        
        // Assert
        var result = actionResult.Result as OkObjectResult;
        var returnContact = result?.Value as ContactDto;
        Assert.Equal(fakeContact, returnContact);
        Assert.Equal(fakeContact.Id, returnContact?.Id);
    }

    [Fact]
    public async void GetContactByName_Returns_Correct_Contact()
    {
        // Arrange 
        var userId = Guid.NewGuid();
        var firstname = "Vorname";
        var lastname = "Nachname";
        var fakeContacts = new List<ContactDto?>();
        var fakeContact = new ContactDto
        {
            Firstname = firstname, Lastname = lastname
        };
        fakeContacts.Add(fakeContact);
        var contactService = A.Fake<IContactService>();
        var rpcClient = A.Fake<IRpcClient>();
        A.CallTo(
            () => contactService.GetByName(firstname, lastname, userId)
        ).Returns(Task.FromResult(fakeContacts));
        ContactController contactController = new(contactService, rpcClient)
        {
            ControllerContext = BindUserToContext(userId)
        };
        
        // Act
        var actionResult = await contactController.GetByName(firstname, lastname);
        
        // Assert
        var result = actionResult.Result as OkObjectResult;
        var returnContacts = result?.Value as IEnumerable<ContactDto>;
        
        Assert.IsType<OkObjectResult>(actionResult.Result as OkObjectResult);
        Assert.Equal(1, returnContacts?.Count());
        Assert.Equal(fakeContact, returnContacts?.First());
    }
    
    // Diese Methode nimmt eine Guid und bindet diese an den HttpContext, wodürch die Controller Methode drauf zugreifen kann
    private ControllerContext BindUserToContext(Guid userId)
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