namespace api_user_management.Dto
{
    public class AuthRefreshDto
    {
        public string AccessToken { get; set; }

        public AuthRefreshDto(string accessToken)
        {
            AccessToken = accessToken;
        }
    }
}