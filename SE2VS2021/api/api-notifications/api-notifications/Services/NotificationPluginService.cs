using System.Reflection;
using api_notifications.Context;
using NotificationPluginBase;

namespace api_notifications.services;

public interface INotificationPluginService
{
    Task<bool> AddPlugin(INotificationPlugin plugin);
    Task<bool> RemovePlugin(INotificationPlugin plugin);
    List<string> GetPluginTypes();
    bool PluginExists(string pluginName);
    INotificationPlugin? GetPlugin(string pluginName);
    Task<bool> LoadPlugins(IConfigurationSection pluginConf);
}

public class NotificationPluginService : INotificationPluginService
{
    private readonly Dictionary<string, INotificationPlugin> _plugins = new();

    public Task<bool> AddPlugin(INotificationPlugin plugin)
    {
        _plugins.Add(plugin.Type, plugin);
        return Task.FromResult(true);
    }

    public Task<bool> RemovePlugin(INotificationPlugin plugin)
    {
        _plugins.Remove(plugin.Type);
        return Task.FromResult(true);
    }

    public List<string> GetPluginTypes()
    {
        return _plugins.Keys.ToList();
    }

    public bool PluginExists(string pluginType)
    {
        return _plugins.ContainsKey(pluginType);
    }
    public INotificationPlugin? GetPlugin(string pluginName)
    {
        _plugins.TryGetValue(pluginName, out var plugin);
        return plugin;
    }

    // Code for Plugin loading and creation

    public Task<bool> LoadPlugins(IConfigurationSection pluginConf)
    {
        try
        {
            var plugins = GetPluginPaths().SelectMany(pluginPath =>
            {
                var pluginAssembly = LoadPlugin(pluginPath);
                return CreatePlugins(pluginAssembly);
            }).ToList();

            if (plugins.Count == 0)
            {
                Console.WriteLine("Could not load any plugin!");
            }
            else
            {
                Console.WriteLine("Plugins: ");
                foreach (var plugin in plugins)
                {
                    if (plugin != null)
                    {
                        Console.WriteLine($"{plugin.Name}\t - {plugin.Description}");
                        Console.WriteLine(
                            $"Trying setting up plugin. Result: {plugin.Setup(pluginConf.GetSection(plugin.Name))}");
                        Console.WriteLine($"Required conf obj: {plugin.UserSpecificConfig()}");
                    }
                }
            }
        }
        catch (Exception ex)
        {
            Console.WriteLine(ex);
        }
        return Task.FromResult(true);
    }

    private static IEnumerable<string> GetPluginPaths()
    {
        var pluginPaths = new List<string>();
        var pluginFolder = Path.Combine(Environment.CurrentDirectory, "plugins");

        Console.WriteLine($"Loading plugins from: {pluginFolder}");
        foreach (var dll in Directory.GetFiles(pluginFolder, "*.dll"))
        {
            pluginPaths.Add(Path.GetFullPath(dll));
        }

        return pluginPaths;
    }

    private static Assembly LoadPlugin(string pluginLocation)
    {
        Console.WriteLine($"Loading plugins from: {pluginLocation}");
        var loadContext = new PluginLoadContext(pluginLocation);
        return loadContext.LoadFromAssemblyName(AssemblyName.GetAssemblyName(pluginLocation));
    }

    private IEnumerable<INotificationPlugin?> CreatePlugins(Assembly assembly)
    {
        var count = 0;

        foreach (var type in assembly.GetTypes())
        {
            if (typeof(INotificationPlugin).IsAssignableFrom(type))
            {
                if (Activator.CreateInstance(type) is INotificationPlugin result)
                {
                    AddPlugin(result);
                    count++;
                    yield return result;
                }
            }
        }

        if (count == 0)
        {
            var availableTypes = string.Join(",", assembly.GetTypes().Select(t => t.FullName));
            throw new ApplicationException(
                $"Can't find any type which implements INotificationPlugin in {assembly} from {assembly.Location}.\n" +
                $"Available types: {availableTypes}");
        }
    }
}