package com.php25.kafka.controller;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author penghuiping
 * @date 2020/7/10 10:01
 */
@RestController
@RequestMapping("/app")
public class AppController {

    @Autowired
    private Processor processor;

    @GetMapping("/index")
    public ResponseEntity<Boolean> index() {
        GenericMessage<String> message = new GenericMessage<>(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss "))+"访问/app/index", Maps.asMap(Sets.newHashSet("type"), (key) -> "log-collector"));
        return ResponseEntity.ok(processor.output().send(message));
    }

    @StreamListener(value = Processor.INPUT, condition = "headers['type']=='log-collector'")
    public void handleMessage(Message<String> message) {
        System.out.println(message.getPayload());
        int i = 1/0;
    }

}
