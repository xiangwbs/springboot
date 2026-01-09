package com.xwbing.service.util;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author daofeng
 * @version $
 * @since 2026年01月09日 14:10
 */
@Slf4j
public class SseUtil {

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
