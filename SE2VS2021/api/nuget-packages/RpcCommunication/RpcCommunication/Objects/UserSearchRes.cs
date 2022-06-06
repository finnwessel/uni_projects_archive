namespace RpcCommunication.Objects;

public class UserSearchRes
{
    public readonly List<SearchUser> Users;

    public UserSearchRes(List<SearchUser> users)
    {
        Users = users;
    }
}