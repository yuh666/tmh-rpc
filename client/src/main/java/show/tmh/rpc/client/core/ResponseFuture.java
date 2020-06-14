package show.tmh.rpc.client.core;

import java.util.concurrent.*;

/**
 * @author zy-user
 */
public class ResponseFuture<T> implements Future<T> {

    private CountDownLatch latch = new CountDownLatch(1);
    private volatile T result;
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

    public void done(T result) {
        this.result = result;
        this.done = true;
        latch.countDown();
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        latch.await();
        return result;
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        latch.await(timeout, unit);
        return result;
    }
}
