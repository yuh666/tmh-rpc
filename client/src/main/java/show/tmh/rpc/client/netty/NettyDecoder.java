package show.tmh.rpc.client.netty;


import com.esotericsoftware.kryo.io.Input;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import show.tmh.rpc.client.protocol.RpcResponse;
import show.tmh.rpc.client.core.ThreadLocalKryo;

import java.io.ByteArrayInputStream;

/**
 * @author zy-user
 */
public class NettyDecoder extends LengthFieldBasedFrameDecoder {

    public NettyDecoder() {
        super(Integer.MAX_VALUE, 0, 4, 0, 0);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf byteBuf = (ByteBuf) super.decode(ctx, in);
        if (byteBuf == null) {
            return null;
        }
        try {
            int len = byteBuf.readInt();
            byte[] bytes = new byte[len];
            byteBuf.readBytes(bytes);
            Input input = new Input(new ByteArrayInputStream(bytes));
            return ThreadLocalKryo.kryo.get().readObject(input, RpcResponse.class);
        } finally {
            byteBuf.release();
        }

    }
}
