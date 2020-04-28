import com.php25.learnnetty.sxsrvm.ASocketClient;
import com.php25.learnnetty.sxsrvm.ResponseFuture;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * @Auther: penghuiping
 * @Date: 2018/7/31 09:40
 * @Description:
 */
@Slf4j
public class ByteBufTest {


    @Test
    public void test() {
        ByteBuf byteBuf0 = Unpooled.buffer(1024);
        byteBuf0.writeBytes("hello".getBytes());

        ByteBuf byteBuf1 = Unpooled.buffer(1024);
        byteBuf1.writeBytes("world".getBytes());

        CompositeByteBuf compositeByteBuf = Unpooled.compositeBuffer();
        compositeByteBuf.addComponents(byteBuf0, byteBuf1);

        log.info("" + compositeByteBuf.readableBytes());
        byte[] bytes = new byte[compositeByteBuf.readableBytes()];
        compositeByteBuf.readBytes(bytes, 0, compositeByteBuf.readableBytes());
        log.info(new String(bytes));
    }



}
