using System.Text;
using Microsoft.Extensions.Options;
using Newtonsoft.Json;
using RabbitMQ.Client;
using VersionLogging.Dto;

namespace VersionLogging;

public interface IVersionLogPublisher
{
    void Log(Guid referenceId, string message, string system, Guid? userId);
}

public class VersionLogPublisher: IVersionLogPublisher
{
    private readonly ConnectionFactory _factory;
    private readonly VersionLoggingSettings _options;
    
    public VersionLogPublisher(IOptions<VersionLoggingSettings> options)
    {
        
        if (options == null)
        {
            throw new Exception("Missing Version Logging Settings configuration");
        }
        _options = options.Value;
        _factory = new ConnectionFactory
        {
            HostName = _options.HostName
        };
    }

    public void Log(Guid referenceId, string message, string system, Guid? userId = null)
    {
        using (var connection = _factory.CreateConnection())
        using (var channel = connection.CreateModel())
        {
            channel.QueueDeclare(queue: _options.QueueName,
                false,
                false,
                false,
                null);

            var versionLogDto = new VersionLogDto(referenceId, message, DateTime.Now);
            var newVersionLogDto = new NewVersionLogDto(system, userId, versionLogDto);
            var body = Encoding.UTF8.GetBytes(JsonConvert.SerializeObject(newVersionLogDto));
            channel.BasicPublish(exchange: "",
                _options.QueueName,
                null, body);
        }
    }
}
