package com.xwbing.web.controller.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xwbing.service.demo.sentinel.degrade.SentinelDegradeService;
import com.xwbing.service.demo.sentinel.flow.SentinelFlowService;

@RequestMapping("/sentinel")
@RestController
public class SentinelController {
    @Autowired
    SentinelFlowService flowService;
    @Autowired
    SentinelDegradeService degradeService;

    @GetMapping("/flow/testBlock")
    public String testBlock() {
        return flowService.testBlock();
    }

    @GetMapping("/flow/testFallback")
    public String testFallback() {
        return flowService.testFallback();
    }

    @GetMapping("/degrade/testDegrade")
    public String testDegrade(@RequestParam int time) {
        return degradeService.testDegrade(time);
    }
}