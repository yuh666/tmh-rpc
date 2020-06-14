package show.tmh.rpc.client.netty;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

/**
 * @author zy-user
 */
public class NettyClient {

    private final Bootstrap bootstrap = new Bootstrap();
    private final EventLoopGroup eventLoopGroupWorker = new NioEventLoopGroup(2);
    private final ConcurrentMap<String, Channel> channelTables = new ConcurrentHashMap<>();

    public static NettyClient INSTANCE = new NettyClient();

    private NettyClient() {
        NettyEncoder nettyEncoder = new NettyEncoder();
        NettyDecoder nettyDecoder = new NettyDecoder();
        NettyClientHandler handler = new NettyClientHandler();
        this.bootstrap.group(this.eventLoopGroupWorker).channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(
                                nettyEncoder,
                                nettyDecoder,
                                handler);
                    }
                });
    }

    public void invoke(String host, int port, Object param) throws ExecutionException, InterruptedException {
        String channelKey = host + ":" + port;
        Channel channel = channelTables.computeIfAbsent(channelKey, (key)
                -> bootstrap.connect(new InetSocketAddress(host, port)).channel()
        );
        channel.writeAndFlush(param);
    }

}
