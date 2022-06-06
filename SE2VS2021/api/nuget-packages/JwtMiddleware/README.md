# Description

This Package provides a Middleware to check if a user is authenticated with a `JsonWebToken`.
The JWT has to be provided in the `Authorization` Header.

Example: `Authorization: Bearer <token>`

# Install

## Requirements

- .net6

## Nuget

Install latest `JwtMiddleware` Package.

## Settings

In `appsettings.json` and `appsettings.Development.json` add the following configuration.

```json
"AuthSettings": {
    "RsaPublicKey": "public_key_of_your_user_management",
    "Issuer": "user-api",
    "Audience": "*-api"
},
```

## Program.cs

Get AuthSettings Configuration Settings
```c#
builder.Services.Configure<AuthSettings>(builder.Configuration.GetSection("AuthSettings"));
```

Register AuthService
```c#
builder.Services.AddScoped<IAuthService, AuthService>();
```

Register Middleware
```c#
app.UseMiddleware<JwtMiddleware.Middleware>();
```

## Controllers

In the api controllers add the `[Auth]` attribute before the specified endpoint.
The example below shows how to retrieve the `Guid` of the authenticated user. 
```c#
[Auth]
[HttpGet("")]
public IActionResult ExampleGetEndpoint() {
    // AuthUserId is set by JwtMiddleware
    var userId = (Guid?) HttpContext.Items["AuthUserId"];

    if (userId == null)
    {
        return BadRequest("No valid user id provides in authentication.");
    }
}
```