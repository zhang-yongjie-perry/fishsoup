package com.fishsoup.fishchat.netty;

import com.alibaba.cloud.nacos.registry.NacosServiceRegistry;
import com.fishsoup.fishchat.registry.ChatNacosRegistration;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.string.StringEncoder;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NettyServer implements Runnable {

    private final RedissonClient redissonClient;

    private final NacosServiceRegistry nacosServiceRegistry;

    private final ChatNacosRegistration chatNacosRegistration;

    @Value("${websocket.port}")
    private int port;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Override
    public void run() {
        register();
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(10);
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) {
                        socketChannel.pipeline()
                            .addLast(new StringEncoder())
                            .addLast(new ByteArrayEncoder())
                            .addLast(new HttpServerCodec())
                            .addLast(new HttpObjectAggregator(65536))
                            .addLast(new WebSocketServerProtocolHandler(contextPath + "/ws"))
                            .addLast(new NettyServerHandler(redissonClient))
                            .addLast(new WebSocketFrameHandler(redissonClient));
                    }
                });
            ChannelFuture cf = serverBootstrap.bind(port).sync();
            cf.channel().closeFuture().sync();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            deregister();
        }
    }

    private void register() {
        nacosServiceRegistry.register(chatNacosRegistration);
    }

    private void deregister() {
        nacosServiceRegistry.deregister(chatNacosRegistration);
    }
}
