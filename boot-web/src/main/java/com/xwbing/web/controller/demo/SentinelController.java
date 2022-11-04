package com.xwbing.web.controller.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.xwbing.service.demo.sentinel.ratelimit.SentinelRateLimitService;

@RequestMapping("/sentinel")
@RestController
public class SentinelController {

    @Autowired
    SentinelRateLimitService helloService;

    @GetMapping("/rateLimit/testBlock")
    public String testBlock() {
        return helloService.testBlock();
    }

    @GetMapping("/rateLimit/testFallback")
    public String testFallback() {
        return helloService.testFallback();
    }
}
