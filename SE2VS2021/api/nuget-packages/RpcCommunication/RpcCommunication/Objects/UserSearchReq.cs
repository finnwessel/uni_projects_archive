namespace RpcCommunication.Objects;

public class UserSearchReq
{
    public readonly string Search;

    public UserSearchReq(string search)
    {
        Search = search;
    }
}