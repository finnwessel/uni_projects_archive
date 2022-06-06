using System.Threading.Tasks;
using api_user_management.Dto;
using api_user_management.Services;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Swashbuckle.AspNetCore.Annotations;

namespace api_user_management.Controllers
{
    [ApiController, Route("[controller]")]
    public class AuthController : Controller
    {
        private readonly IAuthService _authService;

        public AuthController(IAuthService authService)
        {
            _authService = authService;
        }

        [SwaggerResponse(StatusCodes.Status200OK, "Logged in user")]
        [SwaggerResponse(StatusCodes.Status400BadRequest, "Bad login request")]
        [HttpPost("/login")]
        public ActionResult<AuthResponseDto> Authenticate([FromBody] AuthRequestDto authRequest)
        {
            var response = _authService.Authenticate(authRequest);

            if (response == null)
                return BadRequest(new {message = "Username or password is incorrect"});

            return Ok(response);
        }

        [SwaggerResponse(StatusCodes.Status200OK, "Returns newly created user")]
        [SwaggerResponse(StatusCodes.Status400BadRequest, "Returns newly created user")]
        [HttpPost("/register")]
        public async Task<ActionResult<UserDto>> Register([FromBody] NewUserDto userDto)
        {
            var response = await _authService.Register(userDto);
            if (response == null)
            {
                return BadRequest();
            }

            return Ok(response);
        }

        [HttpPost("/refresh")]
        public async Task<ActionResult<AuthRefreshDto?>> RefreshToken(string refreshToken)
        {
            var authRefresh = await _authService.RefreshToken(refreshToken);
            if (authRefresh == null)
            {
                return BadRequest();
            }
            else
            {
                return Ok(authRefresh);
            }
        }

        [HttpGet("/public-key")]
        public ActionResult<string> GetPublicKey()
        {
            return Ok(_authService.GetPublicKey());
        }
    }
}