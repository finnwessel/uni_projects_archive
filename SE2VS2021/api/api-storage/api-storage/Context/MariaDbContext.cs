using api_storage.Models;
using Microsoft.EntityFrameworkCore;

namespace api_storage.Context
{
    public class MariaDbContext : DbContext
    {
        public MariaDbContext(DbContextOptions<MariaDbContext> options) : base(options)
        {
        }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);
            modelBuilder.Entity<UploadedFile>().ToTable("uploaded_file");
        }

        public virtual DbSet<UploadedFile> UploadedFiles { get; set; } = null!;
    }
}