package com.php25.learnnetty.http;

import com.php25.learnnetty.util.JsonUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
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
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpVersion;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @Auther: penghuiping
 * @Date: 2018/8/1 10:05
 * @Description:
 */
@Slf4j
public class HttpClient {
    EventLoopGroup worker;
    Bootstrap bootstrap;

    public HttpClient() {
        //worker负责读写数据
        this.worker = new NioEventLoopGroup();
        //辅助启动类
        this.bootstrap = new Bootstrap();
        //设置线程池
        this.bootstrap.group(this.worker);
        //设置socket工厂
        this.bootstrap.channel(NioSocketChannel.class);
        this.bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        this.bootstrap.option(ChannelOption.TCP_NODELAY, true);
        this.bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
        //设置管道
        this.bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                socketChannel.pipeline()
                        .addLast(new HttpRequestEncoder())
                        .addLast(new HttpResponseDecoder())
                        .addLast(new HttpObjectAggregator(66536))
                        .addLast(new HttpClientHandler());
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

    public void sendMsg(ChannelFuture channelFuture, FullHttpRequest msg) {
        try {
            Channel channel = channelFuture.channel();
            channel.writeAndFlush(msg).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void close() throws InterruptedException {
        this.worker.shutdownGracefully().sync();
    }

    public static void main(String args[]) throws Exception {
        HttpClient echoClient = new HttpClient();
        long now = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            try {
                ChannelFuture channelFuture = echoClient.getConnection("localhost", 8080);
                FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/index.do");
                Person person = new Person();
                person.setId(1L);
                person.setUsername("jack");
                person.setAge(12);
                ByteBuf byteBuf = Unpooled.copiedBuffer(JsonUtil.toJson(person).getBytes());
                request.headers().add("content-length", byteBuf.readableBytes());
                request.content().writeBytes(byteBuf);
                echoClient.sendMsg(channelFuture, request);
            } catch (Exception e) {
                log.error("出错啦!",e);
            }
        }
        log.info("总共耗时{}ms", System.currentTimeMillis() - now);
        echoClient.close();
    }

}
