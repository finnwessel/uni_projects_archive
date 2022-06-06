using api_storage.Context;
using api_storage.Services;
using JwtMiddleware.Helpers;
using JwtMiddleware.Services;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.FileProviders;
using Microsoft.Extensions.Hosting;
using Microsoft.OpenApi.Models;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.

builder.Services.AddControllers();
// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

builder.Services.AddCors(c =>
{
    c.AddPolicy("AllowOrigin", options => options.AllowAnyOrigin().AllowAnyMethod().AllowAnyHeader());
});

builder.Services.AddDbContextPool<MariaDbContext>( 
    options => options.UseMySql(builder.Configuration.GetConnectionString("MariaDbConnectionString"),
        ServerVersion.Parse("14.4.14-mysql"),
        mySqlOptions =>
        {
            mySqlOptions
                .EnableRetryOnFailure(
                    maxRetryCount: 10,
                    maxRetryDelay: TimeSpan.FromSeconds(30),
                    errorNumbersToAdd: null);
        }
    ));

builder.Services.AddSwaggerGen(setup =>
{
    // Include 'SecurityScheme' to use JWT Authentication
    var jwtSecurityScheme = new OpenApiSecurityScheme
    {
        Scheme = "bearer",
        BearerFormat = "JWT",
        Name = "JWT Authentication",
        In = ParameterLocation.Header,
        Type = SecuritySchemeType.Http,
        Description = "Put **_ONLY_** your JWT Bearer token on textbox below!",

        Reference = new OpenApiReference
        {
            Id = "Baerer",
            Type = ReferenceType.SecurityScheme
        }
    };

    setup.AddSecurityDefinition(jwtSecurityScheme.Reference.Id, jwtSecurityScheme);

    setup.AddSecurityRequirement(new OpenApiSecurityRequirement
    {
        { jwtSecurityScheme, Array.Empty<string>() }
    });

});

builder.Services.AddHealthChecks();

builder.Services.Configure<AuthSettings>(builder.Configuration.GetSection("AuthSettings"));
builder.Services.AddScoped<IAuthService, AuthService>();
builder.Services.AddScoped<IFileService, FileService>();

var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

//app.Services.GetService<MariaDbContext>()?.Database.EnsureCreated();

app.UseCors("AllowOrigin");

app.UseHealthChecks("/health");

app.UseHttpsRedirection();

//app.UseAuthorization();

app.UseMiddleware<JwtMiddleware.Middleware>();

app.MapControllers();

// Create Database if not exists
using (var scope = ((IApplicationBuilder) app).ApplicationServices.CreateScope())
{
    using (var context = scope.ServiceProvider.GetService<MariaDbContext>())
        context?.Database.EnsureCreated();
}

// Create Directory for uploads if not exist
Directory.CreateDirectory(Path.Combine(Environment.CurrentDirectory, "uploads"));

app.Run();

public partial class Program
{ 
    //public static string? ConfigurationValue { get; private set; }
}
