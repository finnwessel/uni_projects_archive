using System;
using api_user_management.Controllers;
using api_user_management.Dto;
using api_user_management.Services;
using FakeItEasy;
using Microsoft.AspNetCore.Mvc;
using Xunit;

namespace api_user_management_test.Controller
{
    public class AuthControllerTest
    {
        [Fact]
        public void Authenticate_Returns_Auth_Data()
        {
            // Arrange

            var authRequestDto = A.Dummy<AuthRequestDto>();
            var authResponseDto = A.Dummy<AuthResponseDto>();
            var authService = A.Fake<IAuthService>();
            A.CallTo(() => authService.Authenticate(authRequestDto)).Returns(authResponseDto);
            AuthController authController = new(authService);

            // Act
            var actionResult = authController.Authenticate(authRequestDto);

            // Assert
            var result = actionResult.Result as OkObjectResult;
            var returnedResponseDto = result?.Value as AuthResponseDto;
            Assert.IsType<OkObjectResult>(actionResult.Result as OkObjectResult);
            Assert.NotNull(returnedResponseDto);
        }

        [Fact]
        public async void Register_Returns_Register_Data()
        {
            // Arrange
            UserDto userDto = new()
            {
                Id = Guid.NewGuid(),
                Username = "username",
                Firstname = "firstname",
                Lastname = "lastname",
            };
            var newUserDto = A.Dummy<NewUserDto>();
            var authService = A.Fake<IAuthService>();
            A.CallTo(() => authService.Register(newUserDto)).Returns(userDto);
            AuthController authController = new(authService);

            // Act
            var actionResult = await authController.Register(newUserDto);

            // Assert
            var result = actionResult.Result as OkObjectResult;
            var returnUsers = result?.Value as UserDto;
            Assert.IsType<OkObjectResult>(actionResult.Result as OkObjectResult);
            Assert.Equal(userDto.Id, returnUsers?.Id);
        }

        [Fact]
        public void PublicKey_Returns_Rsa_Public_Key()
        {
            // Arrange
            const string publicKey = "public_key";
            var authService = A.Fake<IAuthService>();
            A.CallTo(() => authService.GetPublicKey()).Returns(publicKey);
            AuthController authController = new(authService);

            // Act
            var actionResult = authController.GetPublicKey();

            // Assert
            var result = actionResult.Result as OkObjectResult;
            var returnedString = result?.Value as string;
            Assert.IsType<OkObjectResult>(actionResult.Result as OkObjectResult);
            Assert.Equal(publicKey, returnedString);
        }
    }
}