using System.Linq;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;
using api_user_management.Dto;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.TestHost;
using Newtonsoft.Json;
using Xunit;

namespace api_user_management_test
{
    public class UserIntegrationTest : IClassFixture<CustomWebApplicationFactory<Program>>
    {
        private readonly CustomWebApplicationFactory<Program> _factory;

        public UserIntegrationTest(CustomWebApplicationFactory<Program> factory)
        {
            _factory = factory;
        }

        [Fact]
        public async void GetUsers_Returns_Users()
        {
            // Arrange
            var client = _factory.CreateClient();
            // Act
            var response = await client.GetAsync("User");

            // Assert
            var data = response.Content;
            Assert.True(response.IsSuccessStatusCode);
        }

        [Fact]
        public async void GetUserById_Returns_User()
        {
            // Arrange
            var userId = TestData.UserDtos.First().Id;
            var client = _factory.CreateClient();

            // Act
            var response = await client.GetAsync($"User/{userId}");

            // Assert
            var data = response.Content;
            Assert.True(response.IsSuccessStatusCode);
        }

        [Fact]
        public async void UpdateUser_Returns_NoContent()
        {
            // Arrange
            var userDto = TestData.UserDtos.First();
            userDto.Username = "updatedUser123";
            var client = _factory.CreateClient();
            var json = JsonConvert.SerializeObject(userDto);
            var data = new StringContent(json, Encoding.UTF8, "application/json");
            // Act
            var response = await client.PutAsync(
                $"User/{userDto.Id}",
                data
            );

            // Assert
            Assert.True(response.IsSuccessStatusCode);
        }

        [Fact]
        public async void CreateUser_Returns_User()
        {
            // Arrange
            NewUserDto newUserDto = new()
            {
                Username = "NewUser123",
                Email = "new-user@example.de",
                Firstname = "new",
                Lastname = "user",
                Password = "password"
            };

            var client = _factory.CreateClient();
            var json = JsonConvert.SerializeObject(newUserDto);
            var data = new StringContent(json, Encoding.UTF8, "application/json");
            // Act
            var response = await client.PostAsync(
                "User",
                data
            );
            var responseContent = await response.Content.ReadAsStringAsync();
            var responseDto = JsonConvert.DeserializeObject<UserDto>(responseContent);

            // Assert
            Assert.True(response.IsSuccessStatusCode);
            Assert.NotNull(responseDto);
            Assert.Equal(newUserDto.Username, responseDto?.Username);
        }

        [Fact]
        public async void DeleteUser_Returns_User()
        {
            // Arrange
            var userDto = TestData.UserDtos.Last();

            var client = _factory.CreateClient();
            // Act
            var response = await client.DeleteAsync(
                $"User/{userDto.Id}"
            );
            var responseContent = await response.Content.ReadAsStringAsync();
            var responseDto = JsonConvert.DeserializeObject<UserDto>(responseContent);

            // Assert
            Assert.True(response.IsSuccessStatusCode);
            Assert.NotNull(responseDto);
            Assert.Equal(userDto.Username, responseDto?.Username);
        }
    }
}