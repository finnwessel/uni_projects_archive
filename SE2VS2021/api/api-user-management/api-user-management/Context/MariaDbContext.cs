using api_user_management.Models;
using Microsoft.EntityFrameworkCore;

namespace api_user_management.Context
{
    public class MariaDbContext : DbContext
    {
        public MariaDbContext(DbContextOptions<MariaDbContext> options) : base(options)
        {
        }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);
            modelBuilder.Entity<User>().ToTable("User");
        }

        public virtual DbSet<User> Users { get; set; }
    }
}