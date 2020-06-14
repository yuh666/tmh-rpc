package show.tmh.rpc.demo.main;

import show.tmh.rpc.client.core.RpcFactory;
import show.tmh.rpc.demo.UserService;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        UserService userService = RpcFactory.create(UserService.class);
        CountDownLatch countDownLatch = new CountDownLatch(10);
        long l = System.currentTimeMillis();
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < 100000; j++) {
                        userService.get(1L);
                    }
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        System.out.println(System.currentTimeMillis() - l);
    }
}
