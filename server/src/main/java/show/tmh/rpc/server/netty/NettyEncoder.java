package show.tmh.rpc.server.netty;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import show.tmh.rpc.client.protocol.RpcResponse;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

/**
 * @author zy-user
 * 增加长度标识 解决粘包分包问题
 */
public class NettyEncoder extends MessageToByteEncoder<RpcResponse> {
    @Override
    protected void encode(ChannelHandlerContext ctx, RpcResponse msg, ByteBuf out) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(msg);
        objectOutputStream.flush();
        byte[] bytes = byteArrayOutputStream.toByteArray();
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }
}
