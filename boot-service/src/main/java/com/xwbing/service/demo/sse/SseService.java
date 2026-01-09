package com.xwbing.service.demo.sse;

import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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
    private final SseDao sseDao;

    public SseEmitter chat(SseChatDTO dto) {
        if (dto.getSessionId() == null) {
            Long sessionId = sseDao.saveSession(dto.getQuestion());
            dto.setSessionId(sessionId);
        } else {
            String title = sseDao.getBySessionId(dto.getSessionId());
            if (StringUtils.isEmpty(title)) {
                sseDao.updateSessionTitle(dto.getSessionId(), dto.getQuestion());
            }
        }
        sseDao.saveRequest(dto);
        if (dto.isDirect()) {
            String direct = sseDao.getDirect(dto.getQuestion());
            sseDao.saveResponse(dto);
            return this.sendMsg(direct);
        } else {
            return this.event(dto);
        }
    }

    private SseEmitter event(SseChatDTO dto) {
        Request request = new Request.Builder()
                .addHeader("content-type", "application/json")
                .url("https://api-igor-nw.ifugle.com/events")
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), JSONUtil.toJsonStr(dto)))
                .build();
        OkHttpClient client = new OkHttpClient
                .Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
//                .hostnameVerifier((hostname, session) -> true)
//                .sslSocketFactory(this.sslSocketFactory(), Platform.get().platformTrustManager())
                .build();
        SseEventSourceListener eventSourceListener = new SseEventSourceListener(sseDao, dto);
        EventSource.Factory factory = EventSources.createFactory(client);
        factory.newEventSource(request, eventSourceListener);
//        RealEventSource eventSource = new RealEventSource(request, sseEventSourceListener);
//        eventSource.connect(client);
        return eventSourceListener.getSseEmitter();
    }

    public void sse(String url, String param, EventSourceListener eventSourceListener) {
        log.info("sse url:{} param:{}", url, param);
        Request request = new Request.Builder()
                .addHeader("content-type", "application/json")
                .url(url)
                .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), param))
                .build();
        OkHttpClient client = new OkHttpClient
                .Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .build();
        EventSource.Factory factory = EventSources.createFactory(client);
        factory.newEventSource(request, eventSourceListener);
    }

    private SseEmitter sendMsg(String msg) {
        log.info("sendSseMsg msg:{}", msg);
        SseEmitter sseEmitter = new SseEmitter(0L);
        CompletableFuture.runAsync(() -> {
            try {
                sseEmitter.send(msg);
                sseEmitter.complete();
            } catch (Exception e) {
                sseEmitter.completeWithError(e);
            }
        });
        sseEmitter.onError(throwable -> log.error("sendSseMsg error", throwable));
        sseEmitter.onTimeout(() -> log.info("sendSseMsg timeout"));
        sseEmitter.onCompletion(() -> log.info("sendSseMsg complete"));
        return sseEmitter;
    }

//    private SSLSocketFactory sslSocketFactory() {
//        try {
//            TrustManager[] trustAllCerts = new TrustManager[]{
//                    new X509TrustManager() {
//                        @Override
//                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
//                        }
//
//                        @Override
//                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
//                        }
//
//                        @Override
//                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//                            return new java.security.cert.X509Certificate[]{};
//                        }
//                    }
//            };
//            SSLContext sslContext = SSLContext.getInstance("SSL");
//            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
//            return sslContext.getSocketFactory();
//        } catch (Exception e) {
//            return null;
//        }
//    }

//    public static void disableSSLCertificateCheck() {
//        try {
//            // 创建信任所有证书的信任管理器
//            TrustManager[] trustAllCerts = new TrustManager[]{
//                    new X509TrustManager() {
//                        public X509Certificate[] getAcceptedIssuers() {
//                            return null;
//                        }
//                        public void checkClientTrusted(X509Certificate[] certs, String authType) {}
//                        public void checkServerTrusted(X509Certificate[] certs, String authType) {}
//                    }
//            };
//
//            // 使用信任所有证书的信任管理器初始化 SSLContext
//            SSLContext sslContext = SSLContext.getInstance("TLS");
//            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
//            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
//            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
//        } catch (Exception e) {
//
//        }
//    }
}