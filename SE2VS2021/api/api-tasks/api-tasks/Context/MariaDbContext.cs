using api_tasks.Models;
using Microsoft.EntityFrameworkCore;

namespace api_tasks.Context
{
    public class MariaDbContext : DbContext
    {
        public MariaDbContext(DbContextOptions<MariaDbContext> options) : base(options)
        {
        }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);
            modelBuilder.Entity<User>().ToTable("users");
            modelBuilder.Entity<Project>().ToTable("projects");
            modelBuilder.Entity<Register>().ToTable("lists");
            modelBuilder.Entity<Tasks>().ToTable("tasks");
        }
        public virtual DbSet<Project> Projects { get; set; } = null!;
        public virtual DbSet<Register> Registers { get; set; } = null!;
        public virtual DbSet<Tasks> Tasks { get; set; } = null!;
        public virtual DbSet<User> Users { get; set; } = null!;
    }
}