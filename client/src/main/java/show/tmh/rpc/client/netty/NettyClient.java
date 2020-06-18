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
        NettyClientHandler handler = new NettyClientHandler();
        NettyExceptionHandler exceptionHandler = new NettyExceptionHandler();
        this.bootstrap.group(this.eventLoopGroupWorker).channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(
                                new NettyEncoder(),
                                new NettyDecoder(),
                                handler,
                                exceptionHandler);
                    }
                });
    }

    public void invoke(String addr, Object param) throws ExecutionException, InterruptedException {
        Channel channel = channelTables.computeIfAbsent(addr, (key)
                        -> {
                    try {
                        String[] split = addr.split(":");
                        return bootstrap.connect(new InetSocketAddress(split[0],
                                Integer.parseInt(split[1]))).sync().channel();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
        );
        if (channel == null) {
            throw new RuntimeException("cannot create channel");
        }
        channel.writeAndFlush(param);
    }

}
