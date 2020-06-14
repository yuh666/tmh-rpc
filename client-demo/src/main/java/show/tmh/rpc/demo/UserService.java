package show.tmh.rpc.demo;

import show.tmh.rpc.client.annotation.RpcMember;

public interface UserService {

    @RpcMember(1)
    User get(Long id);
}
