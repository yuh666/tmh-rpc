package show.tmh.rpc.client.core;

import show.tmh.rpc.client.protocol.RpcResponse;

import java.util.concurrent.*;

/**
 * @author zy-user
 */
public class ResponseFuture implements Future {

    private CountDownLatch latch = new CountDownLatch(1);
    private volatile RpcResponse response;
    private volatile boolean done;


    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    public void done(RpcResponse response) {
        this.response = response;
        this.done = true;
        latch.countDown();
    }

    @Override
    public RpcResponse get() throws InterruptedException, ExecutionException {
        latch.await();
        if (response.getThrowable() != null) {
            throw new ExecutionException(response.getThrowable());
        }
        return response;
    }

    @Override
    public RpcResponse get(long timeout,
            TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        latch.await(timeout, unit);
        if (latch.getCount() == 1) {
            throw new RuntimeException("timeout");
        }
        if (response.getThrowable() != null) {
            throw new ExecutionException(response.getThrowable());
        }
        return response;
    }
}
