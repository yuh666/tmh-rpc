package show.tmh.rpc.server.netty;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import show.tmh.rpc.client.protocol.RpcRequest;
import show.tmh.rpc.server.core.RpcTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author zy-user
 */
@ChannelHandler.Sharable
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private final ExecutorService service = Executors.newFixedThreadPool(10);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
        service.execute(new RpcTask(msg, ctx, System.currentTimeMillis()));
    }
}
