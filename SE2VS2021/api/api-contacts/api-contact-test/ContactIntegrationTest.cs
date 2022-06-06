using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Http.Headers;
using api_contact.Dto;
using Newtonsoft.Json;
using Xunit;

namespace api_contact_test;

public class ContactIntegrationTest : IClassFixture<CustomWebApplicationFactory<Program>>
{
    private readonly CustomWebApplicationFactory<Program> _factory;

    public ContactIntegrationTest(CustomWebApplicationFactory<Program> factory)
    {
        _factory = factory;
    }

    [Fact]
    public async void GetContacts_Returns_Contacts()
    {
        // Arrange
        var client = _factory.CreateClient();
        client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue(TestData.AuthToken);
        // Act
        var response = await client.GetAsync($"/Contact");
        
        // Assert
        var data = response.Content;
        var responseContent = await response.Content.ReadAsStringAsync();
        var responseDto = JsonConvert.DeserializeObject<IEnumerable<ContactDto>>(responseContent);
        Assert.True(response.StatusCode == HttpStatusCode.OK);
        Assert.True(responseDto?.Count() == 2);
    }

    [Fact]
    public async void GetContactsByName_Returns_CorrectContacts()
    {
        // Arrange
        var client = _factory.CreateClient();
        client.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue(TestData.AuthToken);
        // Act
        var firstname = "Markus";
        var lastname = "Müller";
        var response = await client.GetAsync($"Contact/{firstname}, {lastname}");
        
        // Assert
        var data = response.Content;
        var responseContent = await response.Content.ReadAsStringAsync();
        var responseDto = JsonConvert.DeserializeObject<IEnumerable<ContactDto>>(responseContent);
        Assert.True(response.StatusCode == HttpStatusCode.OK);
        Assert.True(responseDto?.Count() == 1);
        Assert.Equal(TestData.NewContactDtos[1].Firstname, responseDto?.First().Firstname);
        Assert.Equal(TestData.NewContactDtos[1].Lastname, responseDto?.First().Lastname);
    }
}