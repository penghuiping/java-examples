package com.php25.learnnetty.http;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

/**
 * @Auther: penghuiping
 * @Date: 2018/8/1 10:10
 * @Description:
 */
@Slf4j
public class HttpClientHandler extends SimpleChannelInboundHandler<FullHttpResponse> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse response) {
        log.info("返回信息:{}", response.content().readUnsignedInt());
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("读取服务器内容出错", cause);
        ctx.close();
    }
}
