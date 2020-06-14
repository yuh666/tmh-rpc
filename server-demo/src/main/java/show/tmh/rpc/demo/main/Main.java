package show.tmh.rpc.demo.main;

import show.tmh.rpc.demo.EchoServiceImpl;
import show.tmh.rpc.demo.UserServiceImpl;
import show.tmh.rpc.server.core.RpcServer;

public class Main {


    public static void main(String[] args) {
        RpcServer rpcServer = new RpcServer("localhost", 9991);
        rpcServer.register(new EchoServiceImpl());
        rpcServer.register(new UserServiceImpl());
        rpcServer.start();
    }
}
