using System.Collections.Concurrent;
using System.Threading.Channels;
using UserNotification.Dto;

namespace api_notifications.services;

public interface IMessageBroker
{
    void Register(Guid userId);
    void Unregister(Guid userId);
    IAsyncEnumerable<Notification> DequeueAsync(Guid userId, CancellationToken cancelToken);
    Task EnqueueAsync(Guid userId, Notification notificationDto, CancellationToken cancelToken);

}

public class MessageBroker : IMessageBroker
{
    private readonly Dictionary<Guid, int> _connectionCount;
    private readonly ConcurrentDictionary<Guid, Channel<Notification>> _clientToChannelMap;
    public MessageBroker()
    {
        _connectionCount = new Dictionary<Guid, int>();
        _clientToChannelMap = new ConcurrentDictionary<Guid, Channel<Notification>>();
    }

    public IAsyncEnumerable<Notification> DequeueAsync(Guid userId, CancellationToken cancelToken)
    {
        if (_clientToChannelMap.TryGetValue(userId, out var channel))
        {
            return channel.Reader.ReadAllAsync(cancelToken);
        }

        throw new ArgumentException($"Id {userId} isn't registered");
    }

    public async Task EnqueueAsync(Guid userId, Notification notificationDto, CancellationToken cancelToken)
    {
        if(_clientToChannelMap.TryGetValue(userId, out var channel))
        {
            await channel.Writer.WriteAsync(notificationDto, cancelToken);
        }
    }

    public void Register(Guid userId)
    {
        if (!_clientToChannelMap.ContainsKey(userId))
        {
            _connectionCount.Add(userId, 1);
            if(!_clientToChannelMap.TryAdd(userId, Channel.CreateUnbounded<Notification>()))
            {
                throw new ArgumentException($"Failed to create channel for {userId}");
            }   
        }
        else
        {
            _connectionCount.TryGetValue(userId, out var count);
            _connectionCount[userId] = count + 1;
        }
    }

    public void Unregister(Guid userId)
    {
        _connectionCount.TryGetValue(userId, out var count);
        if (count - 1 <= 0)
        {
            _connectionCount.Remove(userId, out _);
            _clientToChannelMap.TryRemove(userId, out _);
        }
    }
}