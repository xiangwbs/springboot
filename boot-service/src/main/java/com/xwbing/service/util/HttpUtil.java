package com.xwbing.service.util;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLException;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.xwbing.service.exception.UtilException;

import lombok.extern.slf4j.Slf4j;

/**
 * HttpClientUtil
 *
 * @author xiangwb
 */
@Slf4j
public class HttpUtil {
    private static PoolingHttpClientConnectionManager poolingHttpClientConnectionManager;
    private static final String APPLICATION_JSON = "application/json";
    private static final String FORM_URLENCODED = "application/x-www-form-urlencoded";
    private static final String URL_ERROR = "request url can not be empty";
    private static final String PARAM_ERROR = "request params is null";

    /**
     * 连接池管理器，设置最大连接数、并发连接数
     */
    static {
        poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
        // 最大连接数
        poolingHttpClientConnectionManager.setMaxTotal(100);
        // 并发数
        poolingHttpClientConnectionManager.setDefaultMaxPerRoute(50);
    }

    /***
     * 默认连接配置参数
     */
    private static final RequestConfig REQUEST_CONFIG = RequestConfig.custom().setSocketTimeout(1000)
            .setConnectTimeout(5000).setConnectionRequestTimeout(500).build();
    // Request retry handler
    private static HttpRequestRetryHandler retryHandler = (exception, executionCount, context) -> {
        log.info("retryRequest-->");
        if (executionCount > 5) {
            return false;
        }
        if (exception instanceof InterruptedIOException) {
            // Timeout
            log.error("请求超时");
            return false;
        }
        if (exception instanceof UnknownHostException) {
            // Unknown host
            log.error("未知主机");
            return false;
        }
        if (exception instanceof SSLException) {
            // SSL handshake exception
            log.error("SSL连接失败");
            return false;
        }
        HttpClientContext clientContext = HttpClientContext.adapt(context);
        HttpRequest request = clientContext.getRequest();
        return !(request instanceof HttpEntityEnclosingRequest);
    };

    private static CloseableHttpClient getHttpClient() {
        return HttpClients.custom().setConnectionManager(poolingHttpClientConnectionManager)
                .setDefaultRequestConfig(REQUEST_CONFIG).setRetryHandler(retryHandler).setConnectionManagerShared(true)
                .build();
    }

    /**
     * get请求
     *
     * @param url
     *
     * @return
     */
    public static JSONObject get(String url, JSONObject header) {
        if (StringUtils.isEmpty(url)) {
            throw new IllegalArgumentException(URL_ERROR);
        }
        url = url.replaceAll(" ", "%20");
        HttpGet get = new HttpGet(url);
        Optional.ofNullable(header).orElse(new JSONObject())
                .forEach((key, value) -> get.addHeader(key, Objects.toString(value)));
        return getResult(get);
    }

    /**
     * get请求 带参数
     *
     * @param url
     * @param param
     * @param header
     *
     * @return
     */
    public static JSONObject get(String url, JSONObject param, JSONObject header) throws Exception {
        URIBuilder uriBuilder = new URIBuilder(url);
        if (param != null) {
            param.forEach((key, value) -> {
                // 遍历map,拼接请求参数
                uriBuilder.setParameter(key, Objects.toString(value));
            });
        }
        return get(uriBuilder.toString(), header);
    }

    /**
     * post请求 json
     *
     * @param url
     * @param param
     *
     * @return
     */
    public static JSONObject postByJson(String url, JSONObject param, JSONObject header) {
        if (StringUtils.isEmpty(url)) {
            throw new IllegalArgumentException(URL_ERROR);
        }
        if (param == null) {
            throw new IllegalArgumentException(PARAM_ERROR);
        }
        HttpPost post = new HttpPost(url);// 创建HttpPost的实例
        post.setEntity(new StringEntity(param.toString(), "UTF-8"));// 设置参数到请求对象中
        post.addHeader("Content-Type", APPLICATION_JSON);// 发送json数据需要设置contentType
        Optional.ofNullable(header).orElse(new JSONObject())
                .forEach((key, value) -> post.addHeader(key, Objects.toString(value)));
        return getResult(post);
    }

    /**
     * post请求 form
     *
     * @param url
     * @param param
     *
     * @return
     */
    public static JSONObject postByForm(String url, JSONObject param, JSONObject header) {
        if (StringUtils.isEmpty(url)) {
            throw new IllegalArgumentException(URL_ERROR);
        }
        if (param == null || param.size() == 0) {
            throw new IllegalArgumentException(PARAM_ERROR);
        }
        HttpPost post = new HttpPost(url);
        List<NameValuePair> params = new ArrayList<>();
        param.forEach((key, value) -> params.add(new BasicNameValuePair(key, Objects.toString(value))));
        try {
            post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            post.setHeader("Content-Type", FORM_URLENCODED);
            Optional.ofNullable(header).orElse(new JSONObject())
                    .forEach((key, value) -> post.addHeader(key, Objects.toString(value)));
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage());
            throw new UtilException("postByForm数据转换错误");
        }
        return getResult(post);
    }

    public static JSONObject postFile(String url, File file, JSONObject header) {
        if (StringUtils.isEmpty(url)) {
            throw new IllegalArgumentException(URL_ERROR);
        }
        if (file == null) {
            throw new IllegalArgumentException(PARAM_ERROR);
        }
        HttpPost post = new HttpPost(url);
        HttpEntity requestEntity = MultipartEntityBuilder.create().addPart("file",
                new FileBody(file, ContentType.create("multipart/form-data", Consts.UTF_8), file.getName())).build();
        post.setEntity(requestEntity);
        Optional.ofNullable(header).orElse(new JSONObject())
                .forEach((key, value) -> post.addHeader(key, Objects.toString(value)));
        return getResult(post);
    }

    /**
     * put请求
     *
     * @param url
     * @param param
     *
     * @return
     */
    public static JSONObject put(String url, JSONObject param, JSONObject header) {
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException(URL_ERROR);
        }
        if (param == null || param.size() == 0) {
            throw new IllegalArgumentException(PARAM_ERROR);
        }
        HttpPut put = new HttpPut(url);
        put.setEntity(new StringEntity(param.toString(), "UTF-8"));
        //        put.addHeader("Content-type", APPLICATION_JSON);
        Optional.ofNullable(header).orElse(new JSONObject())
                .forEach((key, value) -> put.addHeader(key, Objects.toString(value)));
        return getResult(put);
    }

    /**
     * delete请求
     *
     * @param url
     *
     * @return
     */
    public static JSONObject delete(String url, JSONObject header) {
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException(URL_ERROR);
        }
        HttpDelete delete = new HttpDelete(url);
        Optional.ofNullable(header).orElse(new JSONObject())
                .forEach((key, value) -> delete.addHeader(key, Objects.toString(value)));
        return getResult(delete);
    }

    /**
     * 获取结果
     *
     * @param request
     *
     * @return
     */
    private static JSONObject getResult(HttpRequestBase request) {
        JSONObject jsonResult = null;
        CloseableHttpClient client = getHttpClient();// 创建HttpClient的实例
        try {
            long start = System.currentTimeMillis();
            CloseableHttpResponse response = client.execute(request);
            long end = System.currentTimeMillis();
            long ms = end - start;
            log.info("{} url:{} 请求时间{}ms", request.getMethod(), request.getURI().toString().replace("%20", " "), ms);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {// 判断网络连接状态码是否正常(0-200都数正常)
                HttpEntity entity = response.getEntity();// 获取结果实体
                if (entity != null) {
                    String result = EntityUtils.toString(entity, "UTF-8");
                    jsonResult = JSONObject.parseObject(result);
                }
            } else {
                log.error(response.getStatusLine().getReasonPhrase());
            }
            response.close();
            return jsonResult;
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new UtilException("请求网络接口错误");
        } catch (JSONException e) {
            throw new UtilException("返回结果不是json");
        } finally {
            poolingHttpClientConnectionManager.closeExpiredConnections();
            poolingHttpClientConnectionManager.closeIdleConnections(120, TimeUnit.MILLISECONDS);
        }
    }
}