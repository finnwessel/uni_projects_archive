using api_notifications.Config;
using api_notifications.Context;
using api_notifications.services;
using JwtMiddleware.Helpers;
using JwtMiddleware.Services;
using Microsoft.EntityFrameworkCore;
using Microsoft.OpenApi.Models;

var builder = WebApplication.CreateBuilder(args);

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

// Load settings
// Important, right click appsettings.json -> Properties -> Copy to output = always
builder.Services.Configure<RabbitSettings>(builder.Configuration.GetRequiredSection("RabbitSettings"));
builder.Services.Configure<AuthSettings>(builder.Configuration.GetSection("AuthSettings"));
// Add services to the container.
builder.Services.AddHostedService<NotificationBackgroundService>();
builder.Services.AddSingleton<IMessageQueue, MessageQueue>();
builder.Services.AddScoped<INotificationService, NotificationService>();
builder.Services.AddScoped<IAuthService, AuthService>();
builder.Services.AddSingleton<INotificationPluginService, NotificationPluginService>();

var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}
//
var pluginSettings = builder.Configuration.GetSection("PluginSettings");
using (var scope = app.Services.CreateScope())
{
    var pluginService = scope.ServiceProvider.GetRequiredService<INotificationPluginService>();
    await pluginService.LoadPlugins(pluginSettings);
}

// Create Database if not exists
using (var scope = ((IApplicationBuilder) app).ApplicationServices.CreateScope())
{
    using (var context = scope.ServiceProvider.GetService<MariaDbContext>())
        context?.Database.EnsureCreated();
}

app.UseCors("AllowOrigin");

//app.UseHttpsRedirection();

app.UseMiddleware<JwtMiddleware.Middleware>();

app.MapControllers();

app.Run();
