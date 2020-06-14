package show.tmh.rpc.client.netty;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import show.tmh.rpc.client.core.FutureCollection;
import show.tmh.rpc.client.core.ResponseFuture;
import show.tmh.rpc.client.protocol.RpcResponse;

/**
 * @author zy-user
 */
@ChannelHandler.Sharable
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {


    private FutureCollection collection = FutureCollection.INSTANCE;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
        Long responseId = msg.getResponseId();
        ResponseFuture responseFuture = collection.get(responseId);
        if (responseFuture != null) {
            responseFuture.done(msg);
        }
    }
}
