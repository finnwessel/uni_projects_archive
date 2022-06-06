namespace RpcCommunication.Objects;

public class UserDetailsRes
{
    public UserDetails? UserDetails;

    public UserDetailsRes(UserDetails? userDetails)
    {
        UserDetails = userDetails;
    }
}