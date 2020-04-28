package com.php25.learnnetty.tcp_unpack;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Auther: penghuiping
 * @Date: 2018/7/30 17:49
 * @Description:
 */
@Slf4j
@ChannelHandler.Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    private static AtomicInteger atomicInteger = new AtomicInteger(0);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String msgStr = (String) msg;
        log.info("Server received: {},计数:{}", msgStr, atomicInteger.get());
        //注意这里不能直接用msg，需要新建一个
        ctx.writeAndFlush(msgStr + ":" + atomicInteger.addAndGet(1) + Constant.DELIMITER)
                .addListeners(ChannelFutureListener.CLOSE);
        ReferenceCountUtil.release(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("出错啦！", cause);
        ctx.close();
    }
}
