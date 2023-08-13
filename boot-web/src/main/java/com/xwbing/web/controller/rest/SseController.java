package com.xwbing.web.controller.rest;

import java.util.Random;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.extern.slf4j.Slf4j;

/**
 * @author daofeng
 * @version $Id$
 * @since 2023年08月13日 下午3:20
 */
@Slf4j
@RestController
@RequestMapping("/sse/")
public class SseController {

    @GetMapping(value = "/msg", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter push() {
        SseEmitter sseEmitter = new SseEmitter(5 * 60 * 1000L);
        String msg = "Server-Sent Events（SSE）是一种用于实现服务器向客户端实时推送数据的Web技术。与传统的轮询和长轮询相比，SSE提供了更高效和实时的数据推送机制。SSE基于HTTP协议，允许服务器将数据以事件流（Event Stream）的形式发送给客户端。客户端通过建立持久的HTTP连接，并监听事件流，可以实时接收服务器推送的数据。";
        CompletableFuture.runAsync(() -> {
            try {
                for (int i = 1; i <= msg.length(); i++) {
                    String content = msg.substring(0, i);
                    sseEmitter.send(content);
                    Thread.sleep(100);
                }
            } catch (Exception e) {
                sseEmitter.completeWithError(e);
            }
            sseEmitter.complete();
        });
        sseEmitter.onError(throwable -> log.error("error", throwable));
        sseEmitter.onTimeout(() -> log.info("timeout"));
        sseEmitter.onCompletion(() -> log.info("complete"));
        return sseEmitter;
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