package show.tmh.rpc.server.netty;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import show.tmh.rpc.client.protocol.RpcRequest;
import show.tmh.rpc.client.protocol.RpcResponse;
import show.tmh.rpc.server.core.RpcServer;
import show.tmh.rpc.server.core.RpcTask;

import java.util.concurrent.*;

/**
 * @author zy-user
 */
@ChannelHandler.Sharable
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private final ThreadPoolExecutor service = new ThreadPoolExecutor(5, 5, 0, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(100), new ThreadPoolExecutor.AbortPolicy());

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
        if(!RpcServer.isRunning()){
            RpcResponse rpcResponse = new RpcResponse();
            // shutdown
            rpcResponse.setResponseCode((byte) 1);
            rpcResponse.setResponseId(msg.getRequestId());
            ctx.channel().writeAndFlush(rpcResponse);
            return;
        }
        try {
            service.execute(new RpcTask(msg, ctx, System.currentTimeMillis()));
        } catch (RejectedExecutionException e) {
            RpcResponse rpcResponse = new RpcResponse();
            // 背压
            rpcResponse.setResponseCode((byte) 2);
            rpcResponse.setResponseId(msg.getRequestId());
            ctx.channel().writeAndFlush(rpcResponse);
        }
    }

    public void shutdown(){
        this.service.shutdown();
    }
}
