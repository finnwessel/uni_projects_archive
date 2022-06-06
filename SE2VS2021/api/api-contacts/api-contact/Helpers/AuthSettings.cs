namespace aapi_contact.Helpers
{
    public class AuthSettings
    {
        public string Secret { get; set; }
        public string RsaPrivateKey { get; set; }
        public string RsaPublicKey { get; set; }
        public string Issuer { get; set; }
        public string Audience { get; set; }
    }
}