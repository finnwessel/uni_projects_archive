using api_user_management.Context;
using api_user_management.Helpers;
using api_user_management.Middleware;
using api_user_management.Services;
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

builder.Services.Configure<RpcSettings>(builder.Configuration.GetSection("RpcSettings"));
builder.Services.AddHealthChecks();
builder.Services.AddHostedService<RpcBackgroundService>();
builder.Services.Configure<AuthSettings>(builder.Configuration.GetSection("AuthSettings"));
builder.Services.AddScoped<IUserService, UserService>();
builder.Services.AddScoped<IAuthService, AuthService>();


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

app.MapControllers();

// Create Database if not exists
using (var scope = ((IApplicationBuilder) app).ApplicationServices.CreateScope())
{
    using (var context = scope.ServiceProvider.GetService<MariaDbContext>())
        context?.Database.EnsureCreated();
}


app.Run();


public partial class Program
{
    //public static string? ConfigurationValue { get; private set; }
}