using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using JsonApiDotNetCore.Resources.Annotations;

namespace api_storage.Models
{
    public class UploadedFile
    {
        [Key] 
        [DatabaseGenerated(DatabaseGeneratedOption.Identity)]
        public virtual Guid Id { get; set; }

        [Attr(PublicName = "owner")]
        public Guid OwnerId { get; set; }
        
        [Attr(PublicName = "public")]
        public bool Public { get; set; }

        [Attr(PublicName = "relativeFilePath")]
        public string RelativeFilePath { get; set; } = null!;
    }
}