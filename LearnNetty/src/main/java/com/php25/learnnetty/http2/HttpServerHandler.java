package com.php25.learnnetty.http2;

import com.php25.learnnetty.util.JsonUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
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

/**
 * @Auther: penghuiping
 * @Date: 2018/7/30 17:49
 * @Description:
 */
@Slf4j
@ChannelHandler.Sharable
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        ByteBuf buf = request.content();
        log.info("request uri: " + request.uri());
        log.info("request method: " + request.method());
        log.info("request header: " + request.headers().toString());
        log.info("request body: " + JsonUtil.toJson(buf.toString(Charset.forName("utf-8"))));
        //注意这里不能直接用msg，需要新建一个
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.content().writeBytes(Unpooled.wrappedBuffer("hello world".getBytes(Charset.defaultCharset())));
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("出错啦！", cause);
        ctx.close();
    }
}
