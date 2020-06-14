package show.tmh.rpc.client.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import show.tmh.rpc.client.protocol.RpcRequest;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

/**
 * @author zy-user
 * 增加长度标识 解决粘包分包问题
 */
@ChannelHandler.Sharable
public class NettyEncoder extends MessageToByteEncoder<RpcRequest> {
    @Override
    protected void encode(ChannelHandlerContext ctx, RpcRequest msg, ByteBuf out) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(msg);
        objectOutputStream.flush();
        byte[] bytes = byteArrayOutputStream.toByteArray();
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }
}
