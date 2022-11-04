package com.xwbing.service.demo.sentinel.flow;

import org.springframework.stereotype.Service;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SentinelFlowService {
    @SentinelResource(value = "testBlock", blockHandlerClass = SentinelFlowService.class, blockHandler = "handleBlock")
    public String testBlock() {
        return "Hello testBlock";
    }

    public static String handleBlock(BlockException e) {
        log.error("Hello block", e);
        return "Hello block";
    }

    @SentinelResource(value = "testFallback", fallback = "handleFallback")
    public String testFallback() {
        return "Hello testFallback";
    }

    public String handleFallback() {
        log.error("Hello fallback");
        return "Hello fallback";
    }
}
