package show.tmh.rpc.server.netty;


import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import show.tmh.rpc.client.protocol.RpcResponse;
import show.tmh.rpc.client.core.ThreadLocalKryo;

import java.io.ByteArrayOutputStream;

/**
 * @author zy-user
 * 增加长度标识 解决粘包分包问题
 */
public class NettyEncoder extends MessageToByteEncoder<RpcResponse> {
    @Override
    protected void encode(ChannelHandlerContext ctx, RpcResponse msg, ByteBuf out) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Kryo kryo = ThreadLocalKryo.kryo.get();
        Output output = new Output(byteArrayOutputStream);
        kryo.writeObject(output, msg);
        output.flush();
        byte[] bytes = byteArrayOutputStream.toByteArray();
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }
}
