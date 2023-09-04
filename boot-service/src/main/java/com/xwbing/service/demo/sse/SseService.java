package com.xwbing.service.demo.sse;

import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.internal.platform.Platform;
import okhttp3.internal.sse.RealEventSource;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.apache.commons.lang3.StringUtils;
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
    public SseEmitter chat(SseChatDTO dto) {
        if (dto.getSessionId() == null) {
            Long sessionId = this.saveSession(dto.getQuestion());
            dto.setSessionId(sessionId);
        } else {
            String title = this.getBySessionId(dto.getSessionId());
            if (StringUtils.isEmpty(title)) {
                this.updateSessionTitle(dto.getSessionId(), dto.getQuestion());
            }
        }
        this.saveRequest(dto);
        if (dto.isDirect()) {
            String direct = this.getDirect(dto.getQuestion());
            this.saveResponse(dto);
            return this.sendMsg(direct);
        } else {
            return this.event(dto);
        }
    }

    private SseEmitter event(SseChatDTO dto) {
        Long requestId = dto.getRequestId();
        SseEmitter sseEmitter = new SseEmitter(0L);
        sseEmitter.onError(throwable -> log.error("sseEvent sseEmitter requestId:{} error", requestId, throwable));
        sseEmitter.onTimeout(() -> log.info("sseEvent sseEmitter requestId:{} timeout", requestId));
        sseEmitter.onCompletion(() -> log.info("sseEvent sseEmitter requestId:{} complete", requestId));
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
                // 数据传输过程失败(中断等) 也要保存响应数据
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
        return sseEmitter;
    }

    private SseEmitter sendMsg(String msg) {
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
        if (dto.getChatResult() != null) {
            // 保存数据
        }
    }

    private void saveRequest(SseChatDTO dto) {
        // 保存数据
        dto.setRequestId(0L);
    }

    private Long saveSession(String title) {
        if (StringUtils.isNotEmpty(title) && title.length() > 20) {
            title = title.substring(0, 20);
        }
        // 修改数据
        return 0L;
    }

    private void updateSessionTitle(Long sessionId, String title) {
        if (title.length() > 20) {
            title = title.substring(0, 20);
        }
        // 修改数据
    }

    private String getBySessionId(Long sessionId) {
        // 获取数据
        return null;
    }

    private String getDirect(String question) {
        // 获取数据
        return null;
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