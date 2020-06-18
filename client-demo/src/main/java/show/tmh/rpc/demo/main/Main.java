package show.tmh.rpc.demo.main;

import show.tmh.rpc.client.core.RpcFactory;
import show.tmh.rpc.demo.User;
import show.tmh.rpc.demo.UserService;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        RpcFactory.init("106.12.15.56:2181");
        UserService userService = RpcFactory.create(UserService.class, 100, TimeUnit.SECONDS);
        CountDownLatch countDownLatch = new CountDownLatch(10);
        long l = System.currentTimeMillis();
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 2; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < 100000; j++) {
                        User user = userService.get(1L);
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();
        System.out.println(System.currentTimeMillis() - l);
    }
}
