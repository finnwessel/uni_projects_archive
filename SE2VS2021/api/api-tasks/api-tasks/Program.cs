using api_tasks.Context;
using api_tasks.Services;
using JwtMiddleware.Helpers;
using JwtMiddleware.Services;
using Microsoft.EntityFrameworkCore;
using Microsoft.OpenApi.Models;
using Pomelo.EntityFrameworkCore.MySql.Infrastructure;
using VersionLogging;

var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

builder.Services.AddCors(c =>
{
    c.AddPolicy("AllowOrigin", options => options.AllowAnyOrigin().AllowAnyMethod().AllowAnyHeader());
});

/*
builder.Services.AddDbContext<MariaDbContext>(
    options => options.UseMySql(
        builder.Configuration.GetConnectionString("MariaDbConnectionString"),
        ServerVersion.Parse("14.4.14-mysql")
    )
);
*/

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
builder.Services.Configure<VersionLoggingSettings>(builder.Configuration.GetSection("VersionLoggingSettings"));
builder.Services.AddScoped<IAuthService, AuthService>();
builder.Services.AddScoped<IProjectService, ProjectService>();
builder.Services.AddScoped<IRegisterService, RegisterService>();
builder.Services.AddScoped<ITaskService, TaskService>();
builder.Services.AddScoped<IVersionLogPublisher, VersionLogPublisher>();

var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseCors("AllowOrigin");

app.UseHealthChecks("/health");

//app.UseHttpsRedirection();

//app.UseAuthorization();

app.UseMiddleware<JwtMiddleware.Middleware>();

app.MapControllers();

// Create Database if not exists
using (var scope = ((IApplicationBuilder) app).ApplicationServices.CreateScope())
{
    using (var context = scope.ServiceProvider.GetService<MariaDbContext>())
        context?.Database.EnsureCreated();
}

app.Run();