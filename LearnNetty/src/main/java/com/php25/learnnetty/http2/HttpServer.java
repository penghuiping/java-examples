package com.php25.learnnetty.http2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

/**
 * @Auther: penghuiping
 * @Date: 2018/7/30 17:52
 * @Description:
 */
public class HttpServer {

    private final int port;

    public HttpServer(int port) {
        this.port = port;
    }


    public void start() throws Exception {
        NioEventLoopGroup boss = new NioEventLoopGroup(1);
        NioEventLoopGroup group = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(boss, group)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer() {
                        @Override
                        protected void initChannel(Channel channel) {
                            channel.pipeline()
                                    .addLast(new HttpRequestDecoder())
                                    .addLast(new HttpResponseEncoder())
                                    .addLast(new HttpObjectAggregator(66536))
                                    .addLast(new HttpServerHandler());
                        }
                    });

            ChannelFuture f = b.bind(port).sync();
            System.out.println(HttpServer.class.getName() + " started and listen on " + f.channel().localAddress());
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
            boss.shutdownGracefully().sync();
        }
    }

    public static void main(String args[]) throws Exception {
        new HttpServer(8080).start();
    }
}
