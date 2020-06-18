package show.tmh.rpc.client.core;

import show.tmh.rpc.client.annotation.RpcMember;
import show.tmh.rpc.client.netty.NettyClient;
import show.tmh.rpc.client.protocol.RpcRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

public class RpcProxy implements InvocationHandler {

    private long timeoutInMills;

    public RpcProxy(long timeoutInMills) {
        this.timeoutInMills = timeoutInMills;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setInterfaceName(method.getDeclaringClass().getName());
        rpcRequest.setMethodCode(method.getAnnotation(RpcMember.class).value());
        rpcRequest.setArgs(args);
        rpcRequest.setExpectTimeOut(timeoutInMills);
        //暂时写死地址
        ResponseFuture future = FutureCollection.INSTANCE.register(rpcRequest.getRequestId());
        NettyClient.INSTANCE.invoke("localhost", 9991, rpcRequest);
        if (timeoutInMills > 0) {
            return future.get(timeoutInMills, TimeUnit.MILLISECONDS);
        } else {
            return future.get();
        }
    }
}
