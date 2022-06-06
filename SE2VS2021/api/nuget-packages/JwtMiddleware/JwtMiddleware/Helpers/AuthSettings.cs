namespace JwtMiddleware.Helpers
{
    public class AuthSettings
    {
        public string RsaPublicKey { get; set; } = null!;
        public string Issuer { get; set; } = null!;
        public string Audience { get; set; } = null!;
    }
}