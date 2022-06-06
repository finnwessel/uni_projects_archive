using System.Text;
using Microsoft.Extensions.Options;
using Newtonsoft.Json;
using RabbitMQ.Client;
using RabbitMQ.Client.Events;
using RpcCommunication;
using RpcCommunication.Objects;


namespace api_user_management.Services;

public class RpcBackgroundService : BackgroundService
{
    private readonly RpcSettings _rpcSettings;
    private IServiceProvider Services { get; }
    private readonly ILogger<RpcBackgroundService> _logger;
    private readonly IConnection _connection;
    private readonly IModel _channel;

    public RpcBackgroundService(IOptions<RpcSettings> rabbitSettings, IServiceProvider services,
        ILogger<RpcBackgroundService> logger)
    {
        _rpcSettings = rabbitSettings.Value;
        Services = services;
        _logger = logger;

        var factory = new ConnectionFactory {HostName = _rpcSettings.HostName};

        // create connection  
        _connection = factory.CreateConnection();

        // create channel  
        _channel = _connection.CreateModel();

        //_channel.ExchangeDeclare("demo.exchange", ExchangeType.Topic);  
        _channel.QueueDeclare(_rpcSettings.QueueName, false, false, false, null);
        //_channel.QueueBind("demo.queue.log", "demo.exchange", "demo.queue.*", null);  
        _channel.BasicQos(0, 1, false);

        _connection.ConnectionShutdown += RabbitMQ_ConnectionShutdown;
    }

    protected override Task ExecuteAsync(CancellationToken stoppingToken)
    {
        stoppingToken.ThrowIfCancellationRequested();

        var consumer = new EventingBasicConsumer(_channel);
        consumer.Received += async (ch, ea) =>
        {
            string? response = null;

            var body = ea.Body.ToArray();
            var props = ea.BasicProperties;
            var replyProps = _channel.CreateBasicProperties();
            replyProps.CorrelationId = props.CorrelationId;

            try
            {
                var message = Encoding.UTF8.GetString(body);

                var obj = JsonConvert.DeserializeObject(message, new JsonSerializerSettings
                {
                    TypeNameHandling = TypeNameHandling.Auto
                });
                if (obj == null)
                {
                    Console.WriteLine("Failed to get request object");
                }
                else
                {
                    try
                    {
                        response = await HandleMessage(obj, stoppingToken);
                    }
                    catch (Exception e)
                    {
                        Console.WriteLine(e);
                    }
                }
            }
            catch (Exception e)
            {
                Console.WriteLine(" [.] " + e.Message);
                response = "";
            }
            finally
            {
                var responseBytes = Encoding.UTF8.GetBytes(response);
                _channel.BasicPublish(exchange: "", routingKey: props.ReplyTo,
                    basicProperties: replyProps, body: responseBytes);
                _channel.BasicAck(deliveryTag: ea.DeliveryTag,
                    multiple: false);
            }
        };

        consumer.Shutdown += OnConsumerShutdown;
        consumer.Registered += OnConsumerRegistered;
        consumer.Unregistered += OnConsumerUnregistered;
        consumer.ConsumerCancelled += OnConsumerConsumerCancelled;

        _channel.BasicConsume(_rpcSettings.QueueName, false, consumer);
        return Task.CompletedTask;
    }

    private async Task<string?> HandleMessage(dynamic obj, CancellationToken stoppingToken)
    {
        Console.WriteLine(obj.GetType());
        string? result;

        using (var scope = Services.CreateScope())
        {
            var service = scope.ServiceProvider.GetRequiredService<IUserService>();
            switch (obj)
            {
                case UserSearchReq userSearchReq:
                    var users = await service.GetUsersMatchingQuery(userSearchReq.Search);
                    result = SerializeObject(new UserSearchRes(users));
                    break;
                case UserDetailsReq userDetailsReq:
                    var userDetails = await service.GetUserDetailsById(userDetailsReq.Id);
                    result = SerializeObject(new UserDetailsRes(userDetails));
                    break;
                default:
                    throw new Exception("No RPC Method exists for RPC Object");
            }
        }

        return result;
    }

    private string SerializeObject(object obj)
    {
        return JsonConvert.SerializeObject(obj, Formatting.Indented, new JsonSerializerSettings
        {
            TypeNameHandling = TypeNameHandling.All
        });
    }

    private void OnConsumerConsumerCancelled(object? sender, ConsumerEventArgs e)
    {
    }

    private void OnConsumerUnregistered(object? sender, ConsumerEventArgs e)
    {
    }

    private void OnConsumerRegistered(object? sender, ConsumerEventArgs e)
    {
    }

    private void OnConsumerShutdown(object? sender, ShutdownEventArgs e)
    {
    }

    private void RabbitMQ_ConnectionShutdown(object? sender, ShutdownEventArgs e)
    {
    }

    public override void Dispose()
    {
        _channel.Close();
        _connection.Close();
        base.Dispose();
    }
}