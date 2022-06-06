namespace api_user_management.Rpc.Types;

public class RpcUserSearch
{
    public readonly string Search;

    public RpcUserSearch(string search)
    {
        Search = search;
    }
}