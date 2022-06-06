using System.Collections.Concurrent;
using System.Text;
using Microsoft.Extensions.Options;
using Newtonsoft.Json;
using RabbitMQ.Client;
using RabbitMQ.Client.Events;
using RpcCommunication.Objects;

namespace RpcCommunication;

public interface IRpcClient
{
    UserSearchRes SearchUser(string search);
    UserDetailsRes GetUserDetails(Guid id);
    void Close();
}

public class RpcClient: IRpcClient
{
    private readonly IConnection _connection;
    private readonly IModel _channel;
    private readonly BlockingCollection<string> _respQueue = new BlockingCollection<string>();
    private readonly IBasicProperties _props;
    private readonly RpcSettings _rpcSettings;

    public RpcClient(IOptions<RpcSettings> options)
    {
        _rpcSettings = options.Value;
        
        var factory = new ConnectionFactory() { HostName = _rpcSettings.HostName };

        _connection = factory.CreateConnection();
        _channel = _connection.CreateModel();
        var replyQueueName = _channel.QueueDeclare().QueueName;
        var consumer = new EventingBasicConsumer(_channel);

        _props = _channel.CreateBasicProperties();
        var correlationId = Guid.NewGuid().ToString();
        _props.CorrelationId = correlationId;
        _props.ReplyTo = replyQueueName;

        consumer.Received += (model, ea) =>
        {
            var body = ea.Body.ToArray();
            var response = Encoding.UTF8.GetString(body);
            if (ea.BasicProperties.CorrelationId == correlationId)
            {
                _respQueue.Add(response);
            }
        };

        _channel.BasicConsume(
            consumer: consumer,
            queue: replyQueueName,
            autoAck: true);
    }

    public UserSearchRes SearchUser(string search)
    {
        var message = SerializeObject(new UserSearchReq(search));
        return ExecuteRpc<UserSearchRes>(message);
    }
    
    public UserDetailsRes GetUserDetails(Guid id)
    {
        var message = SerializeObject(new UserDetailsReq(id));
        return ExecuteRpc<UserDetailsRes>(message);
    }

    private T ExecuteRpc<T>(string message)
    {
        var messageBytes = Encoding.UTF8.GetBytes(message);
        _channel.BasicPublish(
            exchange: "",
            routingKey: _rpcSettings.QueueName,
            basicProperties: _props,
            body: messageBytes);
        return DeserializeObject<T>(_respQueue.Take());
    }

    private T DeserializeObject<T>(string obj)
    {
        var result = JsonConvert.DeserializeObject<T>(obj, new JsonSerializerSettings
        {
            TypeNameHandling = TypeNameHandling.Auto
        });
        if (result == null)
        {
            throw new Exception($"Failed to deserialize to {nameof(T)}");
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

    public void Close()
    {
        _connection.Close();
    }
}