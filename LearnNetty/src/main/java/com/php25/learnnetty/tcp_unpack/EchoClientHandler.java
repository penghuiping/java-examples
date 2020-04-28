package com.php25.learnnetty.tcp_unpack;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @Auther: penghuiping
 * @Date: 2018/8/1 10:10
 * @Description:
 */
@Slf4j
public class EchoClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info((String) msg);
        ReferenceCountUtil.release(msg);
        ctx.close();
    }



}
