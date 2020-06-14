package show.tmh.rpc.demo.main;

import show.tmh.rpc.client.core.RpcFactory;
import show.tmh.rpc.demo.EchoService;
import show.tmh.rpc.demo.UserService;

public class Main {

    public static void main(String[] args) {
        UserService userService = RpcFactory.create(UserService.class);
        long l = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            userService.get(1L);
        }
        System.out.println(System.currentTimeMillis() - l);
    }
}
