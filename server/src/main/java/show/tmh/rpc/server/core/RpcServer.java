package show.tmh.rpc.server.core;

import show.tmh.rpc.client.core.RpcRegistry;
import show.tmh.rpc.server.netty.NettyServer;

import java.util.Set;

public class RpcServer {

    private NettyServer nettyServer;
    private RpcRegistry registry;
    private String host;
    private int port;


    public RpcServer(String host, int port, String zkConnString) {
        this.host = host;
        this.port = port;
        nettyServer = new NettyServer(host, port);
        registry = new RpcRegistry(zkConnString);
    }

    public void register(Object instance) {
        ServerRegistry.INSTANCE.register(instance);
    }

    public void start() {
        nettyServer.start();
        registry.start();
        Set<String> registryAddrs = ServerRegistry.INSTANCE.getRegistryInterfaces();
        registryAddrs.forEach(inter -> {
            try {
                registry.addProvider(inter, host + ":" + port);
                System.out.println("register interface: " + inter + " addr: " + host + ":" + port);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
