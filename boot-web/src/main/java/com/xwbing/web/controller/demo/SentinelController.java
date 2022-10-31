package com.xwbing.web.controller.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xwbing.service.demo.ratelimiter.Sentinel.SentinelService;

@RequestMapping("/sentinel")
@RestController
public class SentinelController {

    @Autowired
    SentinelService helloService;

    @GetMapping("/testBlock")
    public String testBlock() {
        return helloService.testBlock();
    }

    @GetMapping("/testFallback")
    public String testFallback() {
        return helloService.testFallback();
    }
}
