package show.tmh.rpc.client.core;

import show.tmh.rpc.client.annotation.RpcMember;
import show.tmh.rpc.client.netty.NettyClient;
import show.tmh.rpc.client.protocol.RpcRequest;
import show.tmh.rpc.client.protocol.RpcResponse;

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
        long start = System.currentTimeMillis();
        NettyClient.INSTANCE.invoke(addr, rpcRequest);
        RpcResponse response;
        if (timeoutInMills > 0) {
            response = future.get(timeoutInMills, TimeUnit.MILLISECONDS);
        } else {
            response = future.get();
        }
        long latency = System.currentTimeMillis() - start;
        //TODO 处理latency和responseCode

        return response.getResult();
    }
}
