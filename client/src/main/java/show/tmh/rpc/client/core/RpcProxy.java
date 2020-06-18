package show.tmh.rpc.client.core;

import show.tmh.rpc.client.annotation.RpcMember;
import show.tmh.rpc.client.netty.NettyClient;
import show.tmh.rpc.client.protocol.RpcRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

public class RpcProxy implements InvocationHandler {

    private long timeoutInMills;

    private RegistryCache registryCache;

    public RpcProxy(long timeoutInMills, RegistryCache registryCache) {
        this.timeoutInMills = timeoutInMills;
        this.registryCache = registryCache;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest rpcRequest = new RpcRequest();
        String interfaceName = method.getDeclaringClass().getName();
        rpcRequest.setInterfaceName(interfaceName);
        rpcRequest.setMethodCode(method.getAnnotation(RpcMember.class).value());
        rpcRequest.setArgs(args);
        rpcRequest.setExpectTimeOut(timeoutInMills);
        ResponseFuture future = FutureCollection.INSTANCE.register(rpcRequest.getRequestId());
        String addr = registryCache.chooseAddr(interfaceName);
        NettyClient.INSTANCE.invoke(addr, rpcRequest);
        if (timeoutInMills > 0) {
            return future.get(timeoutInMills, TimeUnit.MILLISECONDS);
        } else {
            return future.get();
        }
    }
}
