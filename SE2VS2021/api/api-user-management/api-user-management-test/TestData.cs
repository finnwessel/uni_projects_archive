using System;
using System.Collections.Generic;
using api_user_management.Dto;

namespace api_user_management_test
{
    public static class TestData
    {
        public static readonly string UserPassword = "password";

        public static readonly List<UserDto> UserDtos = new List<UserDto>()
        {
            new()
            {
                Id = Guid.NewGuid(), Username = "user1", Email = "user1@example.de", Firstname = "firstname1",
                Lastname = "lastname1"
            },
            new()
            {
                Id = Guid.NewGuid(), Username = "user2", Email = "user1@example.de", Firstname = "firstname2",
                Lastname = "lastname2"
            },
            new()
            {
                Id = Guid.NewGuid(), Username = "user3", Email = "user1@example.de", Firstname = "firstname3",
                Lastname = "lastname3"
            },
            new()
            {
                Id = Guid.NewGuid(), Username = "user4", Email = "user1@example.de", Firstname = "firstname4",
                Lastname = "lastname4"
            },
            new()
            {
                Id = Guid.NewGuid(), Username = "user5", Email = "user1@example.de", Firstname = "firstname5",
                Lastname = "lastname5"
            }
        };
    }
}