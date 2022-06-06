using api_events.Models;
using Microsoft.EntityFrameworkCore;

namespace api_events.Context
{
    public class MariaDbContext : DbContext
    {
        public MariaDbContext(DbContextOptions<MariaDbContext> options) : base(options)
        {
        }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);
            modelBuilder.Entity<Event>().ToTable("events");
            modelBuilder.Entity<UserEvent>().ToTable("user_event").HasKey(ue=> new {ue.UserId, ue.EventId});;
        }

        public virtual DbSet<Event> Events { get; set; } = null!;
        public virtual DbSet<UserEvent> UserEvent { get; set; } = null!;
    }
}