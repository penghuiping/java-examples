package com.php25.common.websocketsample;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

/**
 * @author penghuiping
 * @date 2019/12/31 15:09
 */
@Slf4j
@SpringBootApplication
@Controller
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class);
    }

    @MessageMapping("/talk")
    @SendToUser("/queue/reply")
    public String talk(
            @Payload String msg,
            @Header("simpSessionId") String sessionId) throws Exception {
        log.info("msg:{},sessionId:{}", msg, sessionId);
        return msg;
    }

    @MessageMapping("/chat")
    @SendTo("/topic/reply")
    public String chat(
            @Payload String msg,
            @Header("simpSessionId") String sessionId) throws Exception {
        log.info("msg:{},sessionId:{}", msg, sessionId);
        return msg;
    }
}
