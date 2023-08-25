package com.xwbing.web.controller.rest;

import com.xwbing.service.demo.sse.SseChatDTO;
import com.xwbing.service.demo.sse.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Random;

/**
 * @author daofeng
 * @version $Id$
 * @since 2023年08月13日 下午3:20
 */
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/sse/")
public class SseController {
    private final SseService sseService;

    @PostMapping(value = "/chat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chat(@RequestBody SseChatDTO dto) {
        return sseService.event(dto);
    }


    @GetMapping(value = "/stock-price", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamStockPrice() {
        SseEmitter emitter = new SseEmitter();
        // 模拟生成实时股票价格并推送给客户端
        Random random = new Random();
        new Thread(() -> {
            try {
                while (true) {
                    // 生成随机的股票价格
                    double price = 100 + random.nextDouble() * 10;
                    // 构造股票价格的消息
                    String message = String.format("%.2f", price);
                    // 发送消息给客户端
                    emitter.send(SseEmitter.event().data(message));
                    // 休眠1秒钟
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        }).start();
        return emitter;
    }
}