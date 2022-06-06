using System;
using System.Collections.Generic;
using System.Linq;
using api_contact.Context;
using api_contact.Models;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Mvc.Testing;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Logging;

namespace api_contact_test
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
                    d => d.ServiceType == typeof(DbContextOptions<MariaDbContext>));
                services.Remove(descriptor);
    
                services.AddDbContext<MariaDbContext>(options =>
                    options.UseMySql(
                        connectionString, ServerVersion.Parse("14.4.14-mysql")));
    
                var serviceProvider = services.BuildServiceProvider();
    
                using (var scope = serviceProvider.CreateScope())
                {
                    var scopedServices = scope.ServiceProvider;
                    var db = scopedServices.GetRequiredService<MariaDbContext>();
                    var logger = scopedServices
                        .GetRequiredService<ILogger<CustomWebApplicationFactory<TProgram>>>();
    
                    db.Database.EnsureCreated();
    
                    try
                    {
                        InitializeDbForTest(db);
                    }
                    catch (Exception exception)
                    {
                        logger.LogError(exception, "An error occurred seeding the " +
                                                   "database with test messages. Error: {Message}", exception.Message);
                    }
                }

            });
            
        }
    
        private void InitializeDbForTest(MariaDbContext context)
        {
            context.Database.EnsureDeleted();
            context.Database.EnsureCreated();

            var userId = TestData.UserId;
    
            foreach (var dto in TestData.NewContactDtos)
            {
                var contact = new Contact
                {
                    UserId = userId, 
                    Addresses = new List<Address>(from a in dto.Addresses
                        select new Address
                            {Country = a.Country, Number = a.Number, Street = a.Street, PostalCode = a.PostalCode, City = a.City}
                        ),
                    Birthday = dto.Birthday,
                    Email = dto.Email,
                    Firstname = dto.Firstname,
                    Lastname = dto.Lastname,
                    PhoneNumber = dto.PhoneNumber
                };
                context.Contacts.Add(contact);
                context.SaveChanges();
                context.Entry(contact).State = EntityState.Detached;
    
                context.SaveChanges();
            }
        }

}
}