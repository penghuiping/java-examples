package com.php25.zookeeper.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author penghuiping
 * @date 2020/7/10 10:01
 */
@RestController
@RequestMapping("/app")
public class AppController {


    @GetMapping("/index")
    public ResponseEntity<Boolean> index() {
        return ResponseEntity.ok(true);
    }


}
