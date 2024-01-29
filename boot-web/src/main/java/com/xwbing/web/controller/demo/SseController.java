package com.xwbing.web.controller.demo;

import com.xwbing.service.demo.sse.SseChatDTO;
import com.xwbing.service.demo.sse.SseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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
        return sseService.chat(dto);
    }
}