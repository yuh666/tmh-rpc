package show.tmh.rpc.demo.main;

import show.tmh.rpc.client.core.RpcFactory;
import show.tmh.rpc.demo.EchoService;
import show.tmh.rpc.demo.UserService;

public class Main {

    public static void main(String[] args) {
        EchoService echoService = RpcFactory.create(EchoService.class);
        UserService userService = RpcFactory.create(UserService.class);
        for (int i = 0; i < 100; i++) {
            System.out.println(echoService.echo("Hello World!"));
            System.out.println(userService.get(1L));
        }


    }
}
