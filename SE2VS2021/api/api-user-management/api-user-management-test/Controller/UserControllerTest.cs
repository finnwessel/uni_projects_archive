using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using api_user_management.Controllers;
using api_user_management.Dto;
using api_user_management.Services;
using FakeItEasy;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Xunit;

namespace api_user_management_test.Controller
{
    [Collection("UserControllerTests")]
    public class UserControllerTest
    {
        [Fact]
        public async void GetAll_Returns_AllUsers()
        {
            // Arrange
            const int userCount = 5;
            var fakeUsers = A.CollectionOfDummy<UserDto>(userCount).ToList();
            var userService = A.Fake<IUserService>();
            A.CallTo(() => userService.GetAll()).Returns(Task.FromResult(fakeUsers));
            UserController userController = new(userService);

            // Act
            var actionResult = await userController.GetAll();

            // Assert
            var result = actionResult.Result as OkObjectResult;
            var returnUsers = result?.Value as IEnumerable<UserDto>;
            Assert.IsType<OkObjectResult>(actionResult.Result as OkObjectResult);
            Assert.Equal(userCount, returnUsers?.Count());
        }

        [Fact]
        public async void GetUser_Returns_Correct_User()
        {
            // Arrange
            var userId = Guid.NewGuid();
            var fakeUser = new UserDto
            {
                Id = userId
            };
            var userService = A.Fake<IUserService>();
            A.CallTo(() => userService.GetById(userId)).Returns(fakeUser);
            UserController userController = new(userService);

            // Act
            var actionResult = await userController.GetUser(userId);

            // Assert
            var result = actionResult.Result as OkObjectResult;
            var returnUser = result?.Value as UserDto;
            Assert.Equal(fakeUser, returnUser);
            Assert.Equal(fakeUser.Id, returnUser?.Id);
        }

        [Fact]
        public async void GetUser_Returns_User_From_HttpContext()
        {
            // Arrange
            var userId = Guid.NewGuid();
            var fakeUser = new UserDto
            {
                Id = userId
            };
            var userService = A.Fake<IUserService>();
            A.CallTo(() => userService.GetById(userId)).Returns(fakeUser);
            UserController userController = new(userService)
            {
                ControllerContext = BindUserToContext(userId)
            };


            // Act
            var actionResult = await userController.GetUserProfile();

            // Assert
            var result = actionResult.Result as OkObjectResult;
            var returnUser = result?.Value as UserDto;
            Assert.Equal(fakeUser, returnUser);
            Assert.Equal(fakeUser.Id, returnUser?.Id);
        }

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

        [Fact]
        public async void GetUser_With_Non_Existing_Returns_NotFound()
        {
            // Arrange
            var userId = Guid.NewGuid();
            UserDto? fakeUser = null;
            var userService = A.Fake<IUserService>();
            A.CallTo(() => userService.GetById(userId)).Returns(fakeUser);
            UserController userController = new(userService);

            // Act
            var actionResult = await userController.GetUser(userId);

            // Assert
            var result = actionResult.Result as NotFoundResult;
            Assert.Equal(StatusCodes.Status404NotFound, result?.StatusCode);
        }

        [Fact]
        public async void UpdateUser_Calls_Service()
        {
            // Arrange
            var fakeUser = new UserDto
            {
                Id = Guid.NewGuid(),
                Username = "username",
                Firstname = "firstname",
                Lastname = "lastname"
            };
            var userService = A.Fake<IUserService>();
            UserController userController = new(userService);

            // Act
            await userController.UpdateUser(fakeUser.Id, fakeUser);

            // Assert
            A.CallTo(() => userService.UpdateUser(fakeUser.Id, fakeUser)).MustHaveHappenedOnceExactly();
        }

        [Fact]
        public async void CreateUser_Returns_Created_User()
        {
            // Arrange
            var userId = Guid.NewGuid();
            var newUser = new NewUserDto()
            {
                Username = "username",
                Firstname = "firstname",
                Lastname = "lastname"
            };
            var userService = A.Fake<IUserService>();
            A.CallTo(() => userService.CreateUser(newUser)).Returns(userId);

            UserController userController = new(userService);
            // Act
            await userController.CreateUser(newUser);

            // Assert
            A.CallTo(() => userService.CreateUser(newUser)).MustHaveHappenedOnceExactly();
        }

        [Fact]
        public async void DeleteUser_Returns_Deleted_User()
        {
            // Arrange
            var userId = Guid.NewGuid();
            var fakeUser = new UserDto
            {
                Id = userId,
                Username = "username",
                Firstname = "firstname",
                Lastname = "lastname"
            };
            var userService = A.Fake<IUserService>();
            A.CallTo(() => userService.GetById(userId)).Returns(fakeUser);
            A.CallTo(() => userService.DeleteUser(fakeUser)).Returns(fakeUser);
            UserController userController = new(userService);

            // Act
            var actionResult = await userController.DeleteUser(userId);

            // Assert
            var result = actionResult.Result as OkObjectResult;
            var returnUser = result?.Value as UserDto;
            Assert.Equal(fakeUser, returnUser);
            Assert.Equal(fakeUser.Id, returnUser?.Id);
        }

        [Fact]
        public async void DeleteUser_With_Non_Existing_Returns_NotFound()
        {
            // Arrange
            var userId = Guid.NewGuid();
            UserDto? fakeUser = null;
            var userService = A.Fake<IUserService>();
            A.CallTo(() => userService.GetById(userId)).Returns(fakeUser);
            UserController userController = new(userService);

            // Act
            var actionResult = await userController.DeleteUser(userId);

            // Assert
            var result = actionResult.Result as NotFoundResult;
            Assert.Equal(StatusCodes.Status404NotFound, result?.StatusCode);
        }

        [Fact]
        public async void DeleteUser_Fails_Returns_BadRequest()
        {
            // Arrange
            var userId = Guid.NewGuid();
            var fakeUser = new UserDto
            {
                Id = userId,
                Username = "username",
                Firstname = "firstname",
                Lastname = "lastname"
            };
            UserDto? failedDeletedUser = null;
            var userService = A.Fake<IUserService>();
            A.CallTo(() => userService.GetById(userId)).Returns(fakeUser);
            A.CallTo(() => userService.DeleteUser(fakeUser)).Returns(failedDeletedUser);
            UserController userController = new(userService);

            // Act
            var actionResult = await userController.DeleteUser(userId);

            // Assert
            var result = actionResult.Result as BadRequestResult;
            Assert.Equal(StatusCodes.Status400BadRequest, result?.StatusCode);
        }
    }
}