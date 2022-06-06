using api_contact.Models;
using Microsoft.EntityFrameworkCore;

namespace api_contact.Context
{
    public class MariaDbContext : DbContext
    {
        public MariaDbContext(DbContextOptions<MariaDbContext> options) : base(options)
        {
        }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);
            modelBuilder.Entity<Contact>().ToTable("Contact");
            modelBuilder.Entity<Address>().ToTable("Address");
        }

        public virtual DbSet<Contact> Contacts { get; set; }
    }
}