using api_version_logging.Models;
using Microsoft.EntityFrameworkCore;

namespace api_version_logging.Context;

public class MariaDbContext : DbContext
{
    public MariaDbContext(DbContextOptions<MariaDbContext> options) : base(options)
    {
    }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        base.OnModelCreating(modelBuilder);
        modelBuilder.Entity<Log>().ToTable("logs");
    }
    public virtual DbSet<Log> Logs { get; set; } = null!;
}