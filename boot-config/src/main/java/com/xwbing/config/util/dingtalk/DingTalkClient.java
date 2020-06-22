package com.xwbing.config.util.dingtalk;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;

/**
 * @author xiangwb
 */
public class DingTalkClient {
    private HttpClient httpclient = HttpClients.createDefault();
    public static final String CHAT_URL = "https://oapi.dingtalk.com/chat/send?access_token=%s";

    public DingTalkClient() {
    }

    /**
     * 发送钉钉机器人消息
     *
     * @param webHook
     * @param secret
     * @param message
     *
     * @return
     *
     * @throws IOException
     */
    public SendResult sendWebHook(String webHook, String secret, Message message) throws IOException {
        if (StringUtils.isNotEmpty(secret)) {
            webHook = dingTalkUrl(webHook, secret);
            if (webHook == null) {
                return SendResult.builder().success(false).errorMsg("加签失败").build();
            }
        }
        return execute(webHook, message);
    }

    /**
     * 发送群消息
     *
     * @param accessToken
     * @param message
     *
     * @return
     *
     * @throws IOException
     */
    public SendResult sendChat(String accessToken, Message message) throws IOException {
        return execute(String.format(DingTalkClient.CHAT_URL, accessToken), message);
    }

    private SendResult execute(String url, Message message) throws IOException {
        SendResult sendResult = new SendResult();
        HttpPost httppost = new HttpPost(url);
        httppost.addHeader("Content-Type", "application/json; charset=utf-8");
        httppost.setEntity(new StringEntity(message.toChatString(), "utf-8"));
        HttpResponse response = this.httpclient.execute(httppost);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String entity = EntityUtils.toString(response.getEntity());
            JSONObject result = JSONObject.parseObject(entity);
            Integer errCode = result.getInteger("errcode");
            sendResult.setErrorCode(errCode);
            sendResult.setSuccess(errCode.equals(0));
            sendResult.setErrorMsg(result.getString("errmsg"));
        }
        return sendResult;
    }

    /**
     * 钉钉安全设置:加签
     *
     * @return url
     */
    private String dingTalkUrl(String webHook, String secret) {
        try {
            Long timestamp = System.currentTimeMillis();
            String stringToSign = timestamp + "\n" + secret;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            String sign = URLEncoder.encode(Base64.getEncoder().encodeToString(signData), "UTF-8");
            return String.format("%s&timestamp=%s&sign=%s", webHook, timestamp, sign);
        } catch (NoSuchAlgorithmException | InvalidKeyException | UnsupportedEncodingException ex) {
            return null;
        }
    }
}