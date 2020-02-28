package com.xwbing.util;

import com.alibaba.fastjson.JSONObject;
import com.xwbing.exception.UtilException;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 作者: xiangwb
 * 说明: HttpClientUtil
 */
public class HttpUtil {
    private static Logger LOGGER = LoggerFactory.getLogger(HttpUtil.class);
    private static PoolingHttpClientConnectionManager poolingHttpClientConnectionManager;
    private static final String APPLICATION_JSON = "application/json";
    private static final String FORM_URLENCODED = "application/x-www-form-urlencoded";
    private static final String URL_ERROR = "request url can not be empty";
    private static final String PARAM_ERROR = "request params is null";

    static {
        poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager();
        // 将最大连接数增加到100
        poolingHttpClientConnectionManager.setMaxTotal(100);
        // 将每个路由基础的连接数增加到20
        poolingHttpClientConnectionManager.setDefaultMaxPerRoute(20);
    }

    /***
     * 默认连接配置参数
     */
    private static final RequestConfig defaultRequestConfig = RequestConfig.custom().setSocketTimeout(600000).setConnectTimeout(600000).build();
    // Request retry handler
    private static HttpRequestRetryHandler retryHandler = (exception, executionCount, context) -> {
        LOGGER.info("retryRequest-->");
        if (executionCount > 5) {
            return false;
        }
        if (exception instanceof InterruptedIOException) {
            // Timeout
            LOGGER.error("请求超时");
            return false;
        }
        if (exception instanceof UnknownHostException) {
            // Unknown host
            LOGGER.error("未知主机");
            return false;
        }
        if (exception instanceof SSLException) {
            // SSL handshake exception
            LOGGER.error("SSL连接失败");
            return false;
        }
        HttpClientContext clientContext = HttpClientContext.adapt(context);
        HttpRequest request = clientContext.getRequest();
        return !(request instanceof HttpEntityEnclosingRequest);
    };

    private static CloseableHttpClient getHttpClient() {
        return HttpClients.custom()
                .setConnectionManager(poolingHttpClientConnectionManager)
                .setDefaultRequestConfig(defaultRequestConfig)
                .setRetryHandler(retryHandler).setConnectionManagerShared(true)
                .build();
    }

    /**
     * post请求 json
     *
     * @param url
     * @param param
     * @return
     */
    public static JSONObject postByJson(String url, JSONObject param) {
        if (StringUtils.isEmpty(url)) {
            throw new IllegalArgumentException(URL_ERROR);
        }
        if (param == null) {
            throw new IllegalArgumentException(PARAM_ERROR);
        }
        LOGGER.info("postByJson request url:{}==================", url);
        HttpPost post = new HttpPost(url);// 创建HttpPost的实例
        post.setEntity(new StringEntity(param.toString(), "UTF-8"));// 设置参数到请求对象中
        post.addHeader("Content-Type", APPLICATION_JSON);// 发送json数据需要设置contentType
        return getResult(post);
    }

    /**
     * post请求 form
     *
     * @param url
     * @param param
     * @return
     */
    public static JSONObject postByForm(String url, Map<String, Object> param) {
        if (StringUtils.isEmpty(url)) {
            throw new IllegalArgumentException(URL_ERROR);
        }
        if (param == null || param.size() == 0) {
            throw new IllegalArgumentException(PARAM_ERROR);
        }
        LOGGER.info("postByForm request url:{}================", url);
        HttpPost post = new HttpPost(url);
        // 创建参数队列
        List<NameValuePair> params = new ArrayList<>();
        for (Map.Entry<String, Object> keys : param.entrySet()) {
            params.add(new BasicNameValuePair(keys.getKey(), Objects.toString(keys.getValue())));
        }
        try {
            post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            post.setHeader("Content-Type", FORM_URLENCODED);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(e.getMessage());
            throw new UtilException("postByForm数据转换错误");
        }
        return getResult(post);
    }

    /**
     * get请求
     *
     * @param url
     * @return
     */
    public static JSONObject get(String url) {
        if (StringUtils.isEmpty(url)) {
            throw new IllegalArgumentException(URL_ERROR);
        }
        LOGGER.info("get request url:{}======================", url);
        HttpGet httpGet = new HttpGet(url);
        return getResult(httpGet);
    }

    /**
     * put请求
     *
     * @param url
     * @param param
     * @return
     */
    public static JSONObject put(String url, JSONObject param) {
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException(URL_ERROR);
        }
        if (param == null || param.size() == 0) {
            throw new IllegalArgumentException(PARAM_ERROR);
        }
        LOGGER.info("put request url:{}====================", url);
        HttpPut put = new HttpPut(url);
        put.setEntity(new StringEntity(param.toString(), "UTF-8"));
        put.addHeader("Content-type", APPLICATION_JSON);
        return getResult(put);
    }

    /**
     * delete请求
     *
     * @param url
     * @return
     */
    public static JSONObject delete(String url) {
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException(URL_ERROR);
        }
        LOGGER.info("delete request url:{}=====================", url);
        HttpDelete delete = new HttpDelete(url);
        return getResult(delete);
    }

    /**
     * 获取结果
     *
     * @param request
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
            LOGGER.info("网络接口请求时间为{} ms=======================", ms);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {// 判断网络连接状态码是否正常(0-200都数正常)
                HttpEntity entity = response.getEntity();// 获取结果实体
                if (entity != null) {
                    String result = EntityUtils.toString(entity, "UTF-8");
                    jsonResult = JSONObject.parseObject(result);
                }
            }
            response.close();
            return jsonResult;
        } catch (IOException e) {
            // result.setSuccess(false);
            // result.setMsg(e.getMessage());
            LOGGER.error(e.getMessage());
            throw new UtilException("请求网络接口错误");
        } finally {
            poolingHttpClientConnectionManager.closeExpiredConnections();
            poolingHttpClientConnectionManager.closeIdleConnections(120, TimeUnit.MILLISECONDS);
        }
    }

    public static void main(String[] args) {
        String url = "http://label.drore.com//gis/mapMain/find.json";
        JSONObject j = new JSONObject();
        JSONObject jr = new JSONObject();
        j.put("pageNo", 1);
        j.put("pageSize", 10);
        jr.put("name", "千岛湖");
        j.put("fields", jr);
        String ret = postByJson(url, j).toString();
        System.out.println(ret);
    }
}
