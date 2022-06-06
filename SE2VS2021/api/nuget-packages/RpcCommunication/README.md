# Description

This Package provides the Class `RpcClient`.
This class exports the following functions:
- `SearchUser(string search)` => returns `UserSearchRes`
- `GetUserDetails(Guid id)` => returns `UserDetailsRes`

# Install

## Requirements

- .net6

## Nuget

Install latest `RpcCommunication` Package.

## Settings

In `appsettings.json` and `appsettings.Development.json` add the following configuration.

```c#
"RpcSettings": {
    "HostName": "host_name_of_rabbitmq_instance",
    "QueueName": "queue_name_of_rpc_queue"
},
```

## Program.cs

Get RpcSettings Configuration Settings
```c#
builder.Services.Configure<RpcSettings>(builder.Configuration.GetSection("RpcSettings"));
```

Register RpcClient
```c#
builder.Services.AddScoped<IRpcClient, RpcClient>();
```

## Controllers or Services

```c#
public class SomeControllerOrService
{
    private readonly IRpcClient _rpcClient;
    
    // DI
    public SomeControllerOrService(IRpcClient rpcClient)
    {
        _rpcClient = rpcClient
    }
    
    // User search
    public void SomeFunctionSearchingForUsers()
    {
        string name = "Peter";
        UserSearchRes res = _rpcClient.SearchUser(name);
        Console.WriteLine(res.Users.Count);
    }
    
    // User Details
    public void SomeFunctionRequestingUserDetails()
    {
        Guid userId = Guid.NewGuid();
        UserDetailsRes? res = _rpcClient.GetUserDetails(Guid userId);
        if (res.UserDetails != null)
        {
            Console.WriteLine(res.UserDetails.Email);
        }
    }
}
```