using System;
using System.IdentityModel.Tokens.Jwt;
using System.Security.Claims;
using System.Security.Cryptography;
using System.Threading.Tasks;
using api_user_management.Dto;
using api_user_management.Helpers;
using Microsoft.Extensions.Options;
using Microsoft.IdentityModel.Tokens;

namespace api_user_management.Services
{
    public interface IAuthService
    {
        AuthResponseDto? Authenticate(AuthRequestDto model);
        Task<UserDto?> Register(NewUserDto userDto);
        Task<AuthRefreshDto?> RefreshToken(string token);
        string GetPublicKey();
        bool IsValidToken(string token);
        SecurityToken? ValidateToken(string token);
    }

    public class AuthService : IAuthService
    {
        private readonly AuthSettings _authSettings;
        private readonly IUserService _userService;

        public AuthService(IOptions<AuthSettings> appSettings, IUserService userService)
        {
            _authSettings = appSettings.Value;
            _userService = userService;
        }

        public AuthResponseDto? Authenticate(AuthRequestDto authRequest)
        {
            var user = _userService.GetUserWithCredentials(authRequest.Username,
                SecurityService.Hash(authRequest.Password));

            // return null if user not found
            if (user == null) return null;

            // authentication successful so generate jwt token
            var token = GenerateJwtToken(user);

            return new AuthResponseDto(user, token);
        }

        public async Task<UserDto?> Register(NewUserDto userDto)
        {
            var id = await _userService.CreateUser(userDto);
            if (id.HasValue)
            {
                return await _userService.GetById(id.Value);
            }

            return null;
        }

        public string GetPublicKey()
        {
            return _authSettings.RsaPublicKey;
        }

        public async Task<AuthRefreshDto?> RefreshToken(string token)
        {
            var user = await _userService.GetById(Guid.NewGuid());
            if (user == null)
            {
                return null;
            }
            else
            {
                return new AuthRefreshDto(GenerateJwtToken(user));
            }
        }

        private string GenerateJwtToken(UserDto userDto)
        {
            var privateKey = Convert.FromBase64String(_authSettings.RsaPrivateKey);

            using RSA rsa = RSA.Create();
            rsa.ImportRSAPrivateKey(privateKey, out _);

            var signingCredentials = new SigningCredentials(new RsaSecurityKey(rsa), SecurityAlgorithms.RsaSha256)
            {
                CryptoProviderFactory = new CryptoProviderFactory {CacheSignatureProviders = false}
            };

            // Generate Token
            var tokenHandler = new JwtSecurityTokenHandler();
            var tokenDescriptor = new SecurityTokenDescriptor
            {
                Audience = _authSettings.Audience,
                Issuer = _authSettings.Issuer,
                Subject = new ClaimsIdentity(new[] {new Claim("id", userDto.Id.ToString())}),
                NotBefore = DateTime.Now,
                Expires = DateTime.UtcNow.AddDays(7),
                SigningCredentials = signingCredentials
            };
            var token = tokenHandler.CreateToken(tokenDescriptor);
            return tokenHandler.WriteToken(token);
        }

        public bool IsValidToken(string token)
        {
            var validatedToken = ValidateToken(token);
            return validatedToken != null;
        }

        public SecurityToken? ValidateToken(string token)
        {
            var publicKey = Convert.FromBase64String(_authSettings.RsaPublicKey);

            using RSA rsa = RSA.Create();
            rsa.ImportRSAPublicKey(publicKey, out _);

            var validationParameters = new TokenValidationParameters
            {
                ValidateIssuer = true,
                ValidateAudience = true,
                ValidateLifetime = true,
                ValidateIssuerSigningKey = true,
                ValidIssuer = _authSettings.Issuer,
                ValidAudience = _authSettings.Audience,
                IssuerSigningKey = new RsaSecurityKey(rsa),
                CryptoProviderFactory = new CryptoProviderFactory()
                {
                    CacheSignatureProviders = false
                }
            };

            try
            {
                var handler = new JwtSecurityTokenHandler();
                handler.ValidateToken(token, validationParameters, out var validatedSecurityToken);
                return validatedSecurityToken;
            }
            catch
            {
                return null;
            }
        }
    }
}