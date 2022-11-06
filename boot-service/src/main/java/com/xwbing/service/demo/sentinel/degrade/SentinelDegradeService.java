package com.xwbing.service.demo.sentinel.degrade;

import org.springframework.stereotype.Service;

import com.alibaba.csp.sentinel.annotation.SentinelResource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SentinelDegradeService {
    @SentinelResource(value = "testDegrade", fallback = "handleDegrade")
    public String testDegrade(int time) {
        if (time == 1) {
            throw new RuntimeException("testDegrade error");
        }
        return "Hello testDegrade";
    }

    public String handleDegrade(int time) {
        log.error("Hello Degrade");
        return "Hello Degrade";
    }
}