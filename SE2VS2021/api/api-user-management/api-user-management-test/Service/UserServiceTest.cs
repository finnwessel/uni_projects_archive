using System;
using System.Linq;
using api_user_management.Context;
using api_user_management.Dto;
using api_user_management.Models;
using api_user_management.Services;
using api_user_management_test.TestOrder;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using Microsoft.VisualStudio.TestPlatform.ObjectModel.Adapter;
using Xunit;
using Xunit.Abstractions;

namespace api_user_management_test.Service
{
    public static class DbOptionsFactory
    {
        static DbOptionsFactory()
        {
            var config = new ConfigurationBuilder()
                .AddJsonFile("appsettings.test.json")
                .Build();
            var connectionString = config["Data:DefaultConnection:ConnectionString"];

            DbContextOptions = new DbContextOptionsBuilder<MariaDbContext>()
                .UseMySql(
                    connectionString,
                    ServerVersion.Parse("14.4.14-mysql")
                ).UseQueryTrackingBehavior(QueryTrackingBehavior.NoTracking);
        }

        public static DbContextOptionsBuilder<MariaDbContext> DbContextOptions { get; }
    }

    public class UserServiceFixture : IDisposable
    {
        private MariaDbContext Context { get; }
        public UserService UserService { get; }

        public UserServiceFixture()
        {
            Context = new MariaDbContext(DbOptionsFactory.DbContextOptions.Options);
            UserService = new UserService(Context);

            Seed();
        }

        public void Dispose()
        {
            Context.Database.ExecuteSqlRaw("DELETE FROM User;");
            //Context.Users.RemoveRange(Context.Users);
            //Context.SaveChanges();
        }

        private void Seed()
        {
            Context.Database.EnsureDeleted();
            Context.Database.EnsureCreated();

            foreach (var dto in TestData.UserDtos)
            {
                var user = new User
                {
                    Id = dto.Id,
                    Username = dto.Username,
                    Email = $"{dto.Firstname}-{dto.Lastname}@example.com",
                    Firstname = dto.Firstname,
                    Lastname = dto.Lastname,
                    Password = TestData.UserPassword
                };
                Context.Users.Add(user);
                Context.SaveChanges();
                Context.Entry(user).State = EntityState.Detached;
            }

            //.State = EntityState.Detached
            Context.SaveChanges();
        }
    }

    [TestCaseOrderer("api_user_management_test.TestOrder.PriorityOrderer", "api-user-management-test")]
    [Collection("UserServiceTests")]
    public class UserServiceTest : IClassFixture<UserServiceFixture>
    {
        private readonly UserServiceFixture _fixture;
        private readonly ITestOutputHelper _testOutputHelper;

        public UserServiceTest(UserServiceFixture fixture, ITestOutputHelper testOutputHelper)
        {
            _fixture = fixture;
            _testOutputHelper = testOutputHelper;
        }

        [Fact, TestPriority(0)]
        public async void GetAll_Returns_All_Users()
        {
            // Arrange
            var userCount = TestData.UserDtos.Count;
            // Act
            var users = await _fixture.UserService.GetAll();

            // Assert
            Assert.Equal(userCount, users.Count);
            _testOutputHelper.WriteLine("Executed: 0 GetAll_Returns_All_Users");
        }

        [Fact, TestPriority(1)]
        public async void GetById_Returns_Correct_User()
        {
            // Arrange
            var userId = TestData.UserDtos.Last().Id;
            // Act
            UserDto? returnedUserDto = await _fixture.UserService.GetById(userId);

            // Assert
            Assert.Equal(userId, returnedUserDto?.Id);
            _testOutputHelper.WriteLine("Executed: 1 GetById_Returns_Correct_User");
        }

        [Fact, TestPriority(2)]
        public void GetUserWithCredentials_Returns_Correct_User()
        {
            // Arrange
            UserDto userDto = TestData.UserDtos.First();
            // Act
            UserDto? returnedUser =
                _fixture.UserService.GetUserWithCredentials(userDto.Username, TestData.UserPassword);
            // Assert
            Assert.NotNull(returnedUser);
            Assert.Equal(userDto.Id, returnedUser?.Id);
            _testOutputHelper.WriteLine("Executed: 2 GetUserWithCredentials_Returns_Correct_User");
        }

        [Fact, TestPriority(3)]
        public async void UpdateUser_Returns_Updated_User()
        {
            // Arrange
            UserDto userDto = TestData.UserDtos.First();
            userDto.Username = "updatedUser123";
            // Act
            UserDto? updatedUser = await _fixture.UserService.UpdateUser(userDto.Id, userDto);

            // Assert
            Assert.NotNull(updatedUser);
            Assert.Equal(userDto.Id, updatedUser?.Id);
            Assert.Equal(userDto.Username, updatedUser?.Username);
            _testOutputHelper.WriteLine("Executed: 3 UpdateUser_Returns_Updated_User");
        }

        [Fact, TestPriority(4)]
        public async void CreateUser_Returns_Id_Of_Created_User()
        {
            // Arrange
            NewUserDto newUserDto = new()
            {
                Firstname = "Hans",
                Lastname = "Meier",
                Password = "password",
                Username = "hans123",
                Email = "hans_meier@example.com"
            };

            // Act
            var createdUserId = await _fixture.UserService.CreateUser(newUserDto);

            // Assert
            Assert.IsType<Guid>(createdUserId);
            Assert.NotNull(createdUserId);
            _testOutputHelper.WriteLine("Executed: 4 CreateUser_Returns_Id_Of_Created_User");
        }

        [Fact, TestPriority(5)]
        public async void DeleteUser_Returns_Deleted_User()
        {
            // Arrange
            UserDto user = TestData.UserDtos.Last();
            // Act
            UserDto? userDto = await _fixture.UserService.DeleteUser(user);

            // Assert
            Assert.Equal(user.Id, userDto?.Id);
            _testOutputHelper.WriteLine("Executed: 5 DeleteUser_Returns_Deleted_User");
        }

        [Fact, TestPriority(6)]
        public void UserExists_Returns_Correct_Boolean()
        {
            // Arrange
            UserDto user = TestData.UserDtos.First();
            // Act
            Boolean exists = _fixture.UserService.UserExists(user.Id);

            // Assert
            Assert.True(exists);
            _testOutputHelper.WriteLine("Executed: 6 UserExists_Returns_Correct_Boolean");
        }
    }
}