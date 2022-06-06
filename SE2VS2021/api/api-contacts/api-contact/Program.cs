using aapi_contact.Helpers;
using api_contact.Context;
using api_contact.Middleware;
using api_contact.Services;
using Microsoft.EntityFrameworkCore;
using RpcCommunication;

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

builder.Services.AddHealthChecks();

builder.Services.Configure<RpcSettings>(builder.Configuration.GetSection("RpcSettings"));
builder.Services.Configure<AuthSettings>(builder.Configuration.GetSection("AuthSettings"));
builder.Services.AddScoped<IContactService, ContactService>();
builder.Services.AddScoped<IAuthService, AuthService>();
builder.Services.AddScoped<IRpcClient, RpcClient>();

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

//app.UseHttpsRedirection();

//app.UseAuthorization();

app.UseMiddleware<JwtMiddleware>();

app.UseAuthorization();

app.MapControllers();

// Create Database if not exists
using (var scope = ((IApplicationBuilder) app).ApplicationServices.CreateScope())
{
    using (var context = scope.ServiceProvider.GetService<MariaDbContext>())
        context?.Database.EnsureCreated();
}

app.Run();

// Make the implicit Program class public so test projects can access it
public partial class Program { }