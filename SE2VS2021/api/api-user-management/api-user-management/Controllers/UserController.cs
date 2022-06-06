using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using api_user_management.Dto;
using api_user_management.Helpers;
using api_user_management.Services;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Swashbuckle.AspNetCore.Annotations;

namespace api_user_management.Controllers
{
    public interface IUserController
    {
        Task<ActionResult<IEnumerable<UserDto>>> GetAll();
        Task<ActionResult<UserDto>> GetUser(Guid id);
        Task<ActionResult<UserDto>> UpdateUser(Guid id, UserDto userDto);
        Task<ActionResult<UserDto>> CreateUser(NewUserDto userDto);
        Task<ActionResult<UserDto>> DeleteUser(Guid id);
    }

    [ApiController]
    [Route("[controller]")]
    public class UserController : Controller, IUserController
    {
        private readonly IUserService _userService;

        public UserController(IUserService userService)
        {
            _userService = userService;
        }

        // GET: /Users
        [SwaggerResponse(StatusCodes.Status200OK, "Returns all existing user")]
        [HttpGet]
        public async Task<ActionResult<IEnumerable<UserDto>>> GetAll()
        {
            return Ok(await _userService.GetAll());
        }

        // GET: /Users/5
        [SwaggerResponse(StatusCodes.Status200OK, "Returns user with given id")]
        [SwaggerResponse(StatusCodes.Status404NotFound, "No user with given id")]
        [HttpGet("{id}")]
        public async Task<ActionResult<UserDto>> GetUser(Guid id)
        {
            //var userId = (int?) HttpContext.Items["AuthUserId"];
            var user = await _userService.GetById(id);

            if (user == null)
            {
                return NotFound();
            }

            return Ok(user);
        }

        // GET: /Users/5
        [SwaggerResponse(StatusCodes.Status200OK, "Returns user with given id")]
        [SwaggerResponse(StatusCodes.Status404NotFound, "No user with given id")]
        [HttpGet("/profile")]
        [Auth]
        public async Task<ActionResult<UserDto>> GetUserProfile()
        {
            var userId = (Guid?) HttpContext.Items["AuthUserId"];

            if (userId == null)
            {
                return BadRequest("No valid user id provides in authentication.");
            }

            var user = await _userService.GetById(userId.Value);

            if (user == null)
            {
                return NotFound("No user with provided id found.");
            }

            return Ok(user);
        }

        // PUT: /Users/5
        [SwaggerResponse(StatusCodes.Status204NoContent, "Updated user")]
        //[SwaggerResponse(StatusCodes.Status404NotFound, "No user exists with given id")]
        [SwaggerResponse(StatusCodes.Status400BadRequest, "Failed to update user")]
        [HttpPut("{id}")]
        public async Task<ActionResult<UserDto>> UpdateUser(Guid id, UserDto userDto)
        {
            if (id != userDto.Id)
            {
                return BadRequest();
            }

            var user = await _userService.UpdateUser(id, userDto);
            if (user == null)
            {
                return BadRequest();
            }

            return NoContent();
        }

        // POST: /Users
        [SwaggerResponse(StatusCodes.Status201Created, "Created user")]
        [SwaggerResponse(StatusCodes.Status400BadRequest, "Failed to create user")]
        [HttpPost]
        public async Task<ActionResult<UserDto>> CreateUser(NewUserDto userDto)
        {
            var userId = await _userService.CreateUser(userDto);
            if (userId == null)
            {
                return BadRequest();
            }

            return CreatedAtAction("GetUser", new UserDto {Id = userId.Value}, userDto);
        }

        // DELETE: /Users/5
        [SwaggerResponse(StatusCodes.Status200OK, "Deleted user")]
        [SwaggerResponse(StatusCodes.Status404NotFound, "No user exists with given id")]
        [SwaggerResponse(StatusCodes.Status400BadRequest, "Failed to delete user")]
        [SwaggerResponse(StatusCodes.Status400BadRequest, "Failed to delete user")]
        [HttpDelete("{id}")]
        public async Task<ActionResult<UserDto>> DeleteUser(Guid id)
        {
            var user = await _userService.GetById(id);
            if (user == null)
            {
                return NotFound();
            }

            var deletedUser = await _userService.DeleteUser(user);

            if (deletedUser == null)
            {
                return BadRequest();
            }

            return Ok(deletedUser);
        }
    }
}