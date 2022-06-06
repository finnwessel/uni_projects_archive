namespace RpcCommunication.Objects;

public class UserDetailsReq
{
    public Guid Id;

    public UserDetailsReq(Guid id)
    {
        Id = id;
    }
}