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
    private static final String CHAT_URL = "https://oapi.dingtalk.com/chat/send?access_token=%s";
    private static final String PC_SLIDE_URL = "dingtalk://dingtalkclient/page/link?pc_slide=true&url=";

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
    public SendResult sendRobot(String webHook, String secret, Message message) throws IOException {
        if (StringUtils.isNotEmpty(secret)) {
            webHook = secret(webHook, secret);
            if (webHook == null) {
                return SendResult.builder().success(false).errorMsg("加签失败").build();
            }
        }
        return execute(webHook, message.toRobotString());
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
        return execute(String.format(DingTalkClient.CHAT_URL, accessToken), message.toChatString());
    }

    private SendResult execute(String url, String body) throws IOException {
        SendResult sendResult = new SendResult();
        HttpPost httppost = new HttpPost(url);
        httppost.addHeader("Content-Type", "application/json; charset=utf-8");
        httppost.setEntity(new StringEntity(body, "utf-8"));
        HttpResponse response = this.httpclient.execute(httppost);
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String entity = EntityUtils.toString(response.getEntity());
            JSONObject result = JSONObject.parseObject(entity);
            sendResult.setMessageId(result.getString("messageId"));
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
    private String secret(String webHook, String secret) {
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

    /**
     * pc端右边开启小窗
     *
     * @param linkUrl
     *
     * @return
     */
    public static String toPcSlide(String linkUrl) {
        try {
            return PC_SLIDE_URL + URLEncoder.encode(linkUrl, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return linkUrl;
        }
    }
}