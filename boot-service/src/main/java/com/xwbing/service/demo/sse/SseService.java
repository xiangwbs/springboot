package com.xwbing.service.demo.sse;

import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.internal.platform.Platform;
import okhttp3.internal.sse.RealEventSource;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author daofeng
 * @version SseServiceImpl$
 * @since 2023年08月18日 1:46 PM
 */
@RequiredArgsConstructor
@Slf4j
@Service
public class SseService {
    public SseEmitter event(SseChatDTO dto) {
        Long requestId = dto.getRequestId();
        SseEmitter sseEmitter = new SseEmitter(0L);
        Request request = new Request.Builder()
                .addHeader("content-type", "application/json")
                .url("xxx")
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), JSONUtil.toJsonStr(dto)))
                .build();
        RealEventSource eventSource = new RealEventSource(request, new EventSourceListener() {
            @Override
            public void onOpen(EventSource eventSource, Response response) {
                // 记录响应时间
                dto.setResponseDate(new Date());
                log.info("sseEvent onOpen requestId:{}", requestId);
            }

            @Override
            public void onEvent(EventSource eventSource, String id, String type, String data) {
                try {
                    log.info("sseEvent onEvent requestId:{} data:{}", requestId, data);
                    sseEmitter.send(data);
                    // 记录响应数据
                    dto.setChatResult(data);
                } catch (Exception e) {
                    // 关闭eventSource
                    eventSource.cancel();
                }
            }

            @Override
            public void onClosed(EventSource eventSource) {
                log.info("sseEvent onClosed requestId:{}", requestId);
                // 数据传输完成 保存响应数据
                saveResponse(dto);
                // 关闭sseEmitter
                sseEmitter.complete();
            }

            @Override
            public void onFailure(EventSource eventSource, Throwable t, Response response) {
                log.info("sseEvent onFailure requestId:{} error", requestId, t);
                // 保存响应数据
                saveResponse(dto);
                // 关闭sseEmitter
                sseEmitter.complete();
            }
        });
        OkHttpClient client = new OkHttpClient
                .Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .hostnameVerifier((hostname, session) -> true)
                .sslSocketFactory(this.sslSocketFactory(), Platform.get().platformTrustManager())
                .build();
        eventSource.connect(client);

        sseEmitter.onError(throwable -> {
            log.error("sseEvent sseEmitter requestId:{} error", requestId, throwable);
        });
        sseEmitter.onTimeout(() -> {
            log.info("sseEvent sseEmitter requestId:{} timeout", requestId);
        });
        sseEmitter.onCompletion(() -> {
            log.info("sseEvent sseEmitter requestId:{} complete", requestId);
        });
        return sseEmitter;
    }

    public SseEmitter sendMsg(String msg) {
        log.info("sendSseMsg msg:{}", msg);
        SseEmitter sseEmitter = new SseEmitter(0L);
        CompletableFuture.runAsync(() -> {
            try {
                sseEmitter.send(msg);
            } catch (Exception e) {
                sseEmitter.completeWithError(e);
            }
            sseEmitter.complete();
        });
        sseEmitter.onError(throwable -> log.error("sendSseMsg error", throwable));
        sseEmitter.onTimeout(() -> log.info("sendSseMsg timeout"));
        sseEmitter.onCompletion(() -> log.info("sendSseMsg complete"));
        return sseEmitter;
    }

    private void saveResponse(SseChatDTO dto) {
        log.info("saveResponse dto:{}", JSONUtil.toJsonStr(dto));
        if (dto.getChatResult() != null) {
            // 保存数据
        }
    }

    private SSLSocketFactory sslSocketFactory() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            return sslContext.getSocketFactory();
        } catch (Exception e) {
            return null;
        }
    }
}