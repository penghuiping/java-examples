package com.php25.learnnetty.http;

import com.php25.learnnetty.util.JsonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Auther: penghuiping
 * @Date: 2018/7/30 17:49
 * @Description:
 */
@Slf4j
@ChannelHandler.Sharable
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    static AtomicInteger count = new AtomicInteger(0);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest obj) {
        ByteBuf buf = obj.content();
        log.info("request uri: " + obj.uri());
        log.info("request method: " + obj.method());
        log.info("request header: " + obj.headers().toString());
        log.info("request body: " + JsonUtil.toJson(buf.toString(Charset.forName("utf-8"))));
        log.info("msg内容:{},计数:{}", buf.toString(Charset.defaultCharset()), count.getAndIncrement());
        log.info("");
        log.info("");

        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, Unpooled.copyInt(count.get()));
        ctx.channel().writeAndFlush(fullHttpResponse);
        ctx.close();
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("出错啦！", cause);
        ctx.close();
    }
}
