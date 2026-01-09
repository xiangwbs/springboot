package com.xwbing.service.util;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
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
        OkHttpClient.Builder builder = new OkHttpClient
                .Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS);
        if (url.startsWith("https")) {
            // 忽略主机名校验
            builder.hostnameVerifier((hostname, session) -> true);
            // 信任所有证书
            X509TrustManager trustManager = getTrustManager();
            SSLContext sslContext = getSslContext(trustManager);
            if (sslContext != null) {
                builder.sslSocketFactory(sslContext.getSocketFactory(), trustManager);
            }
        }
        OkHttpClient client = builder.build();
        EventSource.Factory factory = EventSources.createFactory(client);
        factory.newEventSource(request, eventSourceListener);
    }

    private static X509TrustManager getTrustManager() {
        return new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString) {
                // 信任所有客户端证书
            }

            @Override
            public void checkServerTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString) {
                // 信任所有服务器证书
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
    }

    private static SSLContext getSslContext(TrustManager trustManager) {
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[]{trustManager}, new SecureRandom());
            return sslContext;
        } catch (Exception e) {
            log.info("getSslContext error", e);
        }
        return null;
    }

    public static void disableSSLCertificateCheck() {
        try {
            // 创建信任所有证书的信任管理器
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            // 使用信任所有证书的信任管理器初始化 SSLContext
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
        } catch (Exception e) {

        }
    }
}
