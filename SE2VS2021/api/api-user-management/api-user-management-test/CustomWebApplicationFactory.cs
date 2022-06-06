using System;
using System.Linq;
using api_user_management.Context;
using api_user_management.Models;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Mvc.Testing;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;

namespace api_user_management_test
{
    public class CustomWebApplicationFactory<TProgram>
        : WebApplicationFactory<TProgram> where TProgram : class
    {
        protected override void ConfigureWebHost(IWebHostBuilder builder)
        {
            var config = new ConfigurationBuilder()
                .AddJsonFile("appsettings.test.json")
                .Build();
            var connectionString = config["Data:DefaultConnection:ConnectionString"];

            builder.ConfigureServices(services =>
            {
                var descriptor = services.SingleOrDefault(
                    d => d.ServiceType ==
                         typeof(DbContextOptions<MariaDbContext>));

                services.Remove(descriptor);

                services.AddDbContext<MariaDbContext>(options =>
                    options.UseMySql(
                        connectionString,
                        ServerVersion.Parse("14.4.14-mysql")
                    )
                );

                var sp = services.BuildServiceProvider();

                using (var scope = sp.CreateScope())
                {
                    var scopedServices = scope.ServiceProvider;
                    var db = scopedServices.GetRequiredService<MariaDbContext>();
                    var logger = scopedServices
                        .GetRequiredService<ILogger<CustomWebApplicationFactory<TProgram>>>();

                    db.Database.EnsureCreated();

                    try
                    {
                        InitializeDbForTests(db);
                    }
                    catch (Exception ex)
                    {
                        logger.LogError(ex, "An error occurred seeding the " +
                                            "database with test messages. Error: {Message}", ex.Message);
                    }
                }
            });
        }

        private void InitializeDbForTests(MariaDbContext context)
        {
            context.Database.EnsureDeleted();
            context.Database.EnsureCreated();

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
                context.Users.Add(user);
                context.SaveChanges();
                context.Entry(user).State = EntityState.Detached;
            }

            //.State = EntityState.Detached
            context.SaveChanges();
        }
    }
}