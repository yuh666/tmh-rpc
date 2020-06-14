package show.tmh.rpc.server.netty;


import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author zy-user
 */
public class NettyServer {

    private final ServerBootstrap bootstrap = new ServerBootstrap();
    private final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private final EventLoopGroup workerGroup = new NioEventLoopGroup(2);
    private final ConcurrentMap<String, Channel> channelTables = new ConcurrentHashMap<>();

    private final String host;
    private final int port;


    public NettyServer(String host, int port) {
        this.host = host;
        this.port = port;
        NettyEncoder nettyEncoder = new NettyEncoder();
        NettyDecoder nettyDecoder = new NettyDecoder();
        NettyServerHandler nettyServerHandler = new NettyServerHandler();
        this.bootstrap.group(this.bossGroup, this.workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.SO_KEEPALIVE, false)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast(
                                        nettyEncoder,
                                        nettyDecoder,
                                        nettyServerHandler
                                );
                    }
                });

    }

    public void start() {
        try {
            ChannelFuture sync = this.bootstrap.bind(new InetSocketAddress(host, port)).sync();
            sync.channel().closeFuture().sync();
        } catch (InterruptedException e1) {
            throw new RuntimeException("this.serverBootstrap.bind().sync() InterruptedException", e1);
        } finally {
            this.bossGroup.shutdownGracefully();
            this.workerGroup.shutdownGracefully();
        }
    }

}
