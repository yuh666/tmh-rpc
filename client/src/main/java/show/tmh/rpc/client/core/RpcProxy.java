package show.tmh.rpc.client.core;

import show.tmh.rpc.client.annotation.RpcMember;
import show.tmh.rpc.client.netty.NettyClient;
import show.tmh.rpc.client.protocol.RpcRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class RpcProxy implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setInterfaceName(method.getDeclaringClass().getName());
        rpcRequest.setMethodCode(method.getAnnotation(RpcMember.class).value());
        rpcRequest.setArgs(args);
        //暂时写死地址
        ResponseFuture future = FutureCollection.INSTANCE.register(rpcRequest.getRequestId());
        NettyClient.INSTANCE.invoke("localhost", 9991, rpcRequest);
        return future.get();
    }
}
