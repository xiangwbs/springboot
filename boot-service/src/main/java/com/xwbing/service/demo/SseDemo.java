package com.xwbing.service.demo;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author daofeng
 * @version $
 * @since 2026年01月09日 08:55
 */
@Slf4j
@RequestMapping("/sse")
@RequiredArgsConstructor
@RestController
public class SseDemo {
    @GetMapping("/data")
    public Object getData(@RequestParam(defaultValue = "false") boolean stream) throws IOException {
        Map<String, Object> param = new HashMap<>();
        param.put("user", "测试人员");
        param.put("response_mode", "streaming");
        Map<String, Object> inputs = new HashMap<>();
        inputs.put("query", "写一首诗");
        param.put("inputs", inputs);
        if (stream) {
            SseEmitter sseEmitter = new SseEmitter(0L);
            sseEmitter.onError(throwable -> log.error("sseEvent sseEmitter error", throwable));
            sseEmitter.onTimeout(() -> log.info("sseEvent sseEmitter timeout"));
            sseEmitter.onCompletion(() -> log.info("sseEvent sseEmitter complete"));
            Map<String, String> headMap = new HashMap<>();
            headMap.put("Authorization", "Bearer app-zIUHZ3XRQoqIXQmCrUYsEmlu");
            sse("http://10.40.70.175/v1/workflows/run", JSONUtil.toJsonStr(param), headMap, new EventSourceListener() {
                @Override
                public void onOpen(EventSource eventSource, Response response) {
                    log.info("sseEvent onOpen");
                }

                @Override
                public void onEvent(EventSource eventSource, String id, String type, String data) {
                    try {
                        log.info("sseEvent onEvent data {}", data);
                        sseEmitter.send(data);
                    } catch (Exception e) {
                        log.info("sseEvent onEvent error", e);
                        eventSource.cancel();
                    }
                }

                @Override
                public void onClosed(EventSource eventSource) {
                    log.info("sseEvent onClosed");
                    sseEmitter.complete();
                }

                @Override
                public void onFailure(EventSource eventSource, Throwable t, Response response) {
                    log.error("sseEvent onFailure error:{}", t != null ? ExceptionUtils.getStackTrace(t) : response.message());
                    sseEmitter.complete();
                }
            });
            return sseEmitter;
        } else {
            param.put("response_mode", "blocking");
            return HttpRequest
                    .post("http://10.40.70.175/v1/workflows/run")
                    .header("Authorization", "Bearer app-zIUHZ3XRQoqIXQmCrUYsEmlu")
                    .header("Content-Type", "application/json")
                    .body(JSONUtil.toJsonStr(param))
                    .execute()
                    .body();
        }
    }

    public static void sse(String url, String param, Map<String, String> headerMap, EventSourceListener eventSourceListener) {
        log.info("sse url:{} param:{}", url, param);
        headerMap.put("content-type", "application/json");
        Headers headers = Headers.of(headerMap);
        Request request = new Request.Builder()
                .headers(headers)
                .url(url)
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), param))
                .build();
        OkHttpClient client = new OkHttpClient
                .Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
        EventSource.Factory factory = EventSources.createFactory(client);
        factory.newEventSource(request, eventSourceListener);
    }
}
