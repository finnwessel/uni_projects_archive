using System.IdentityModel.Tokens.Jwt;
using System.Security.Cryptography;
using aapi_contact.Helpers;
using Microsoft.Extensions.Options;
using Microsoft.IdentityModel.Tokens;

namespace api_contact.Services
{
    public interface IAuthService
    {
        string GetPublicKey();
        bool IsValidToken(string token);
        SecurityToken? ValidateToken(string token);
    }

    public class AuthService : IAuthService
    {
        private readonly AuthSettings _authSettings;

        public AuthService(IOptions<AuthSettings> appSettings)
        {
            _authSettings = appSettings.Value;
        }
        
        public string GetPublicKey()
        {
            return _authSettings.RsaPublicKey;
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