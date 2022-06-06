using api_storage.Services;
using api_storage.Structs;
using JwtMiddleware.Helpers;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.StaticFiles;

namespace api_storage.Controllers
{
    [ApiController, Route("[controller]")]
    public class FileController : Controller
    {
        private readonly IWebHostEnvironment _webHostingEnvironment;
        private readonly IFileService _fileService;

        public FileController(IWebHostEnvironment webHostingEnvironment, IFileService fileService)
        {
            _webHostingEnvironment = webHostingEnvironment;
            _fileService = fileService;
        }
        
        [HttpGet("/{fileId}")]
        public async Task<IActionResult>  DownloadFile(string fileId)
        {
            var savedFile = await _fileService.GetUploadedFile(Guid.Parse(fileId));
            if (savedFile is null)
            {
                return NotFound();
            }
            string filePath = Path.Combine(_webHostingEnvironment.ContentRootPath, "./uploads", savedFile.RelativeFilePath);
            if (System.IO.File.Exists(filePath))
            {
                new FileExtensionContentTypeProvider().TryGetContentType(filePath, out var contentType);
                return PhysicalFile(filePath, contentType ?? "", Path.GetFileName(filePath));
            }
            throw new FileNotFoundException();
        }

        [HttpDelete("/{fileId}")]
        public async Task<IActionResult> DeleteFile(string fileId)
        {
            var userId = (Guid?) HttpContext.Items["AuthUserId"];
            if (userId == null)
            {
                return BadRequest("No valid user id provided in authentication.");
            }
            var parsedFileId = Guid.Parse(fileId);
            var savedFile = await _fileService.GetUploadedFile(parsedFileId);
            if (savedFile is null)
            {
                return NotFound();
            }

            if (savedFile.OwnerId.Equals(userId))
            {
                string filePath = Path.Combine(_webHostingEnvironment.ContentRootPath, "./uploads", savedFile.RelativeFilePath);
                if (System.IO.File.Exists(filePath))
                {
                    System.IO.File.Delete(filePath);
                    _fileService.DeleteFile(parsedFileId);
                }    
            }
            else
            {
                return Unauthorized();
            }
            
            throw new FileNotFoundException();
        }

        [HttpGet("/own")]
        [Auth]
        public async Task<IActionResult> GetOwnFiles()
        {
            var userId = (Guid?) HttpContext.Items["AuthUserId"];
            if (userId == null)
            {
                return BadRequest("No valid user id provided in authentication.");
            }
            return Ok(await _fileService.GetOwnFiles(userId.Value));
        }

        [HttpPost("/upload")]
        [Auth]
        public async Task<IActionResult> OnPostUploadAsync(List<IFormFile> files)
        {
            var userId = (Guid?) HttpContext.Items["AuthUserId"];
            if (userId == null)
            {
                return BadRequest("No valid user id provided in authentication.");
            }

            var size = files.Sum(f => f.Length);
            
            var fileList = new FileList
            {
                OwnerId = userId.Value
            };

            foreach (var formFile in files)
            {
                if (formFile.Length > 0)
                {
                    var fileName = Path.GetRandomFileName() + Path.GetExtension(formFile.FileName);
                    var filePath = Path.Combine("./uploads", fileName);

                    await using (var stream = System.IO.File.Create(filePath))
                    {
                        await formFile.CopyToAsync(stream);
                    }
                    fileList.Files.Add(new FileData {IsPublic = true, RelativeFilePath = fileName});
                }
            }
            
            var resultIds = await _fileService.AddUploadedFile(fileList);

            if (resultIds == null)
            {
                return BadRequest("FileData(s) could not be added");
            }
            
            return Ok(resultIds);
        }
    }
}