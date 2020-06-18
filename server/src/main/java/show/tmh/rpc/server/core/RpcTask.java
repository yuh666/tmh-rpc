package show.tmh.rpc.server.core;

import io.netty.channel.ChannelHandlerContext;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import show.tmh.rpc.client.protocol.RpcRequest;
import show.tmh.rpc.client.protocol.RpcResponse;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;

/**
 * @author zy-user
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RpcTask implements Runnable {


    private RpcRequest request;
    private ChannelHandlerContext ctx;
    private long addTime;

    @Override
    public void run() {
        if (System.currentTimeMillis() - addTime > request.getExpectTimeOut()) {
            //超时 直接丢弃
            return;
        }
        Method methodHandle =
                ServerRegistry.INSTANCE.getMethod(request.getInterfaceName(),
                        request.getMethodCode());
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setResponseId(request.getRequestId());
        if (methodHandle == null) {
            rpcResponse.setThrowable(new Exception(
                    request.getInterfaceName() + "#" + request.getMethodCode() + "Not Found!"));
            ctx.channel().writeAndFlush(rpcResponse);
            return;
        }
        Object instance = ServerRegistry.INSTANCE.getInstance(request.getInterfaceName());
        if (instance == null) {
            rpcResponse.setThrowable(new Exception(request.getInterfaceName() + "Not Found!"));
            ctx.channel().writeAndFlush(rpcResponse);
            return;
        }
        try {
            Object res = methodHandle.invoke(instance, request.getArgs());
            rpcResponse.setResult(res);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            rpcResponse.setThrowable(throwable);
        }
        if (System.currentTimeMillis() - addTime > request.getExpectTimeOut()) {
            //超时 直接丢弃
            return;
        }
        ctx.channel().writeAndFlush(rpcResponse);
    }
}
