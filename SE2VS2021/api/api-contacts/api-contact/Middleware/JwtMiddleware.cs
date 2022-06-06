using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using api_contact.Services;

namespace api_contact.Middleware
{
    public class JwtMiddleware
    {
        private readonly RequestDelegate _next;

        public JwtMiddleware(RequestDelegate next)
        {
            _next = next;
        }

        public async Task Invoke(HttpContext context, IAuthService authService)
        {
            var token = context.Request.Headers["Authorization"].FirstOrDefault()?.Split(" ").Last();

            if (token != null)
                AttachUserToContext(context, authService, token);

            await _next(context);
        }

        private void AttachUserToContext(HttpContext context, IAuthService authService,
            string token)
        {
            try
            {
                var jwtToken = (JwtSecurityToken?) authService.ValidateToken(token);
                Console.WriteLine(jwtToken);
                if (jwtToken == null) return;
                var userId = jwtToken.Claims.First(x => x.Type == "id").Value;
                // attach user to context on successful jwt validation
                //var user = userService.GetById(userId);

                var identity = new ClaimsIdentity(new List<Claim>
                {
                    new Claim("Id", userId, ClaimValueTypes.String)
                }, "JWT");
                context.User = new ClaimsPrincipal(identity);

                context.Items["AuthUserId"] = Guid.Parse(userId);
            }
            catch (Exception e)
            {
                Console.WriteLine(e);
                // jwt validation failed, do nothing
            }
        }
    }
}