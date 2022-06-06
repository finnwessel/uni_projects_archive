namespace api_user_management.Services
{
    public static class SecurityService
    {
        public static string Hash(string password)
        {
            return "pretend_like_this_is_hashed:" + password;
        }
    }
}