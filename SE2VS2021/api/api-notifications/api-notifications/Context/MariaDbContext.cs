using api_notifications.Models;
using Microsoft.EntityFrameworkCore;

namespace api_notifications.Context;

public class MariaDbContext : DbContext
{
    public MariaDbContext(DbContextOptions<MariaDbContext> options) : base(options)
    {
    }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        base.OnModelCreating(modelBuilder);
        modelBuilder.Entity<NotificationSettings>().ToTable("notifications");
    }
    public virtual DbSet<NotificationSettings> NotificationSettings { get; set; } = null!;
}