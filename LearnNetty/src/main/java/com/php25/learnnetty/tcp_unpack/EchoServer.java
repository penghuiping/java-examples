package com.php25.learnnetty.tcp_unpack;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @Auther: penghuiping
 * @Date: 2018/7/30 17:52
 * @Description:
 */
public class EchoServer {

    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }


    public void start() throws Exception {
        NioEventLoopGroup boss = new NioEventLoopGroup(1);
        NioEventLoopGroup group = new NioEventLoopGroup(Runtime.getRuntime().availableProcessors());
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.option(ChannelOption.SO_REUSEADDR,true);
            b.group(boss, group)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer() {
                        @Override
                        protected void initChannel(Channel channel) {
                            channel.pipeline()
                                    //DelimiterBasedFrameDecoder+StringDecoder用于解决拆包与粘包问题
                                    .addLast(new DelimiterBasedFrameDecoder(1024, Unpooled.wrappedBuffer(Constant.DELIMITER.getBytes())))
                                    .addLast(new StringDecoder())
                                    .addLast(new StringEncoder())
                                    .addLast(new EchoServerHandler());
                        }
                    });

            ChannelFuture f = b.bind(port).sync();
            System.out.println(EchoServer.class.getName() + " started and listen on " + f.channel().localAddress());
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
            boss.shutdownGracefully().sync();
        }
    }

    public static void main(String args[]) throws Exception {
        new EchoServer(11111).start();
    }
}
