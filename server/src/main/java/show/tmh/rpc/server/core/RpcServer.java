package show.tmh.rpc.server.core;

import show.tmh.rpc.server.netty.NettyServer;

public class RpcServer {

    private NettyServer nettyServer;

    public RpcServer(String host, int port) {
        nettyServer = new NettyServer(host, port);
    }

    public void register(Object instance) {
        ServerRegistry.INSTANCE.register(instance);
    }

    public void start() {
        nettyServer.start();
    }

}
