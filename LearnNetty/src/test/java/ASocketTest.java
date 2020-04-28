import com.php25.learnnetty.sxsrvm.ASocketClient;
import com.php25.learnnetty.sxsrvm.ResponseFuture;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: penghuiping
 * @date: 2019/2/1 16:49
 * @description:
 */
@Slf4j
public class ASocketTest {

    private static final String tmp0100401 = "0005730100401                       1011040000201001000010.211.55.2         k15486410042450320190128100324                                                                                                                                              0100401092019/01/2810:03:24                                                                        1  1  10                                                                       310109199107114515                                                                                                                                       ";
    private static final String tmp0100501 = "0004120100501                       1011040000201001000010.211.55.2         k15486529404090620190128132220                                                                                                                                              0100501092019/01/2813:22:17                                                                          1  1 25ZGSWX001                31010919910711451510000201036961636407";

    @Test
    public void test2() throws Exception {
        ASocketClient socketClient = new ASocketClient();
        long start = System.currentTimeMillis();
        try {
            Future<String> future = socketClient.sendMsg(tmp0100401);
            String result = future.get(5, TimeUnit.SECONDS);
            //String result = future.get();
            log.info("result:{}", result);
        } finally {
            long end = System.currentTimeMillis();
            log.info("耗时为{}ms", (end - start));
            socketClient.close();
        }
    }

    @Test
    public void test3() throws Exception {
        //测试FutureObject的原子性
        ExecutorService executorService = new ThreadPoolExecutor(10, 100, 30, TimeUnit.SECONDS, new LinkedBlockingQueue(128));

        ResponseFuture responseFuture = new ResponseFuture();
        for (int i = 0; i < 100; i++) {
            final int j = i;
            executorService.submit(() -> {
                responseFuture.setOutcome("" + j);
            });
        }

        while (true) {
            log.info("结果:{}", responseFuture.get(10, TimeUnit.SECONDS));
            Thread.sleep(100);
        }
    }
}
