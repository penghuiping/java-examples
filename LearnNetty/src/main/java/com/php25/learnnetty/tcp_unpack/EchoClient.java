package com.php25.learnnetty.tcp_unpack;

import com.php25.learnnetty.util.RandomUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.FutureListener;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @Auther: penghuiping
 * @Date: 2018/8/1 10:05
 * @Description:
 */
@Slf4j
public class EchoClient {
    EventLoopGroup worker;
    Bootstrap bootstrap;

    public EchoClient() {
        //worker负责读写数据
        worker = new NioEventLoopGroup();
        //辅助启动类
        this.bootstrap = new Bootstrap();
        //设置线程池
        bootstrap.group(worker);
        //设置socket工厂
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, false);
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
        bootstrap.option(ChannelOption.SO_REUSEADDR, true);
        //设置管道
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline()
                        //DelimiterBasedFrameDecoder+StringDecoder用于解决拆包与粘包问题
                        .addLast(new DelimiterBasedFrameDecoder(4096, Unpooled.wrappedBuffer(Constant.DELIMITER.getBytes())))
                        .addLast(new StringEncoder())
                        .addLast(new StringDecoder())
                        .addLast(new EchoClientHandler());
            }
        });
    }

    /**
     * 获取连接
     *
     * @return
     * @throws InterruptedException
     */
    private ChannelFuture getConnection(String host, int port) throws InterruptedException {
        return this.bootstrap.connect(new InetSocketAddress(host, port)).sync();
    }


    public void sendMsg(ChannelFuture channelFuture, String msg) {
        try {
            Channel channel = channelFuture.channel();
            channel.writeAndFlush(msg).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void close() throws InterruptedException {
        worker.shutdownGracefully().sync();
    }

    public static void main(String args[]) throws Exception {
        EchoClient echoClient = new EchoClient();
        for (int i = 0; i < 100000; i++) {
            try {
                ChannelFuture channelFuture = echoClient.getConnection("localhost", 11111);
                echoClient.sendMsg(channelFuture, RandomUtil.getRandomLetters(RandomUtil.getRandom(20)) + Constant.DELIMITER);
            }catch (Exception e) {
                log.error("出错啦",e);
            }

        }
        echoClient.close();
    }

}
