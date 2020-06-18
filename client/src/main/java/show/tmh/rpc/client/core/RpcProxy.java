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
        long begin = System.currentTimeMillis();
        RpcRequest rpcRequest = new RpcRequest();
        String interfaceName = method.getDeclaringClass().getName();
        rpcRequest.setInterfaceName(interfaceName);
        rpcRequest.setMethodCode(method.getAnnotation(RpcMember.class).value());
        rpcRequest.setArgs(args);
        ResponseFuture future = FutureCollection.INSTANCE.register(rpcRequest.getRequestId());
        while (System.currentTimeMillis() - begin < timeoutInMills) {
            String addr = registryCache.chooseAddr(interfaceName);
            long restTime = timeoutInMills - ((System.currentTimeMillis() - begin));
            rpcRequest.setExpectTimeOut(restTime);
            try {
                NettyClient.INSTANCE.invoke(addr, rpcRequest);
            } catch (Exception e) {
                continue;
            }
            RpcResponse response;
            if (timeoutInMills > 0) {
                response = future.get(restTime, TimeUnit.MILLISECONDS);
            } else {
                response = future.get();
            }
            if (response.getResponseCode() == 1) {
                System.out.println("优雅");
                registryCache.removeProvider(interfaceName, addr);
                continue;
            }
            return response.getResult();
        }
        throw new RuntimeException("timeout");
    }
}
