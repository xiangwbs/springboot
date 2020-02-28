package com.xwbing.config.util.dingTalk;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * @author xiangwb
 */
public class DingTalkClient {
    private HttpClient httpclient = HttpClients.createDefault();

    public DingTalkClient() {
    }

    /**
     * 发送钉钉消息
     *
     * @param webHook
     * @param secret
     * @param message
     * @return
     * @throws IOException
     */
    public SendResult send(String webHook, String secret, Message message) throws IOException {
        SendResult sendResult = new SendResult();
        if (StringUtils.isNotEmpty(secret)) {
            webHook = dingTalkUrl(webHook, secret);
            if (webHook == null) {
                sendResult.setSuccess(false);
                sendResult.setErrorMsg("加签失败");
                return sendResult;
            }
        }
        HttpPost httppost = new HttpPost(webHook);
        httppost.addHeader("Content-Type", "application/json; charset=utf-8");
        httppost.setEntity(new StringEntity(message.toJsonString(), "utf-8"));
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
