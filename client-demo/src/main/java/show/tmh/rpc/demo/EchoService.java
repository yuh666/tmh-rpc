package show.tmh.rpc.demo;

import show.tmh.rpc.client.annotation.RpcMember;

/**
 * @author zy-user
 */
public interface EchoService {


    @RpcMember(1)
    String echo(String words);
}
