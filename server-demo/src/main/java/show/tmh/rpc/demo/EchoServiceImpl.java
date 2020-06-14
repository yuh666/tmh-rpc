package show.tmh.rpc.demo;

import show.tmh.rpc.client.annotation.RpcMember;

/**
 * @author zy-user
 */
public class EchoServiceImpl implements EchoService {

    @Override
    public String echo(String words) {
        return words;
    }
}
