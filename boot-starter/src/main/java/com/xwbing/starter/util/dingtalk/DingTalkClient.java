package com.xwbing.starter.util.dingtalk;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author xiangwb
 */
@Slf4j
public class DingTalkClient {
    private static HttpClient httpclient = HttpClients.createDefault();
    private static final String CHAT_URL = "https://oapi.dingtalk.com/chat/send?access_token=%s";
    private static final String PC_SLIDE_URL = "dingtalk://dingtalkclient/page/link?pc_slide=true&url=";
    private static final String WEBHOK = "https://oapi.dingtalk.com/robot/send?access_token=da82d4099a3f2515480f35210cc17dd02f315e99537d29cd3de1a751e551b670";
    private static final String secret = "SECa17df9ae897fb39d525a173c47dcc93a6b01133fbb4bf3e659060cd4ed4539ea";

    public DingTalkClient() {
    }

    // ---------------------- Robot ----------------------

    public static SendResult sendErrorMessage(String title, String env, String param, Exception e, boolean atAll,
            String... atMobiles) {
        MarkdownMessage message = new MarkdownMessage();
        message.setTitle(title);
        if (StringUtils.isNotEmpty(env)) {
            message.addItem(MarkdownMessage.getBoldText("环境: " + env));
        }
        message.addItem(MarkdownMessage.getBoldText("参数:"));
        message.addItem(param);
        message.addItem(MarkdownMessage.getBoldText("异常信息:"));
        message.addItem(ExceptionUtils.getStackTrace(e));
        message.setAtAll(atAll);
        message.addAtMobiles(Arrays.asList(atMobiles));
        return sendRobotMessage(message);
    }

    /**
     * 钉钉机器人发送link
     *
     * @param linkMessage
     *
     * @return
     */
    public static SendResult sendRobotMessage(LinkMessage linkMessage) {
        try {
            SendResult send = sendRobot(WEBHOK, secret, linkMessage);
            if (!send.isSuccess()) {
                log.error("{} - {}", linkMessage.getTitle(), send.toString());
            }
            return send;
        } catch (Exception e) {
            log.error("{} - {}", linkMessage.getTitle(), ExceptionUtils.getStackTrace(e));
            return SendResult.builder().success(false).build();
        }
    }

    /**
     * 钉钉机器人发送text
     *
     * @param title
     * @param atAll
     * @param atMobiles
     * @param params
     *
     * @return
     */
    public static SendResult sendRobotMessage(String title, boolean atAll, List<String> atMobiles, Object... params) {
        StringBuilder content = new StringBuilder("title: ").append(title).append("\n").append("\n");
        int i = 1;
        for (Object obj : params) {
            content.append("params").append(i).append(": ").append(obj).append("\n");
            i++;
        }
        try {
            TextMessage textMessage = new TextMessage(content.toString());
            textMessage.addAtMobiles(atMobiles);
            textMessage.setAtAll(atAll);
            SendResult send = sendRobot(WEBHOK, secret, textMessage);
            if (!send.isSuccess()) {
                log.error("{} - {}", title, send.toString());
            }
            return send;
        } catch (Exception e) {
            log.error("{} - {}", title, ExceptionUtils.getStackTrace(e));
            return SendResult.builder().success(false).build();
        }
    }

    /**
     * 钉钉机器人发送markdown
     *
     * @param markdownMessage
     */
    public static SendResult sendRobotMessage(MarkdownMessage markdownMessage) {
        try {
            //title当做一级标题
            markdownMessage.addItem(0, MarkdownMessage.getHeaderText(1, markdownMessage.getTitle()));
            SendResult send = sendRobot(WEBHOK, secret, markdownMessage);
            if (!send.isSuccess()) {
                log.error("{} - {}", markdownMessage.getTitle(), send.toString());
            }
            return send;
        } catch (Exception e) {
            log.error("{} - {}", markdownMessage.getTitle(), ExceptionUtils.getStackTrace(e));
            return SendResult.builder().success(false).build();
        }
    }
    // ---------------------- Chat ----------------------

    /**
     * 发送钉钉群消息
     *
     * @param markdownMessage
     * @param accessToken
     *
     * @return
     */
    public static SendResult sendChatMessage(MarkdownMessage markdownMessage, String accessToken) {
        try {
            markdownMessage.addItem(0, MarkdownMessage.getHeaderText(1, markdownMessage.getTitle()));
            SendResult send = sendChat(accessToken, markdownMessage);
            if (!send.isSuccess()) {
                log.error("{} - {}", markdownMessage.getTitle(), JSONObject.toJSON(send));
            }
            return send;
        } catch (Exception e) {
            log.error("{} - {}", markdownMessage.getTitle(), ExceptionUtils.getStackTrace(e));
            return SendResult.builder().success(false).build();
        }
    }

    /**
     * 发送钉钉群消息
     *
     * @param linkMessage
     * @param accessToken
     *
     * @return
     */
    public static SendResult sendChatMessage(LinkMessage linkMessage, String accessToken) {
        try {
            SendResult send = sendChat(accessToken, linkMessage);
            if (!send.isSuccess()) {
                log.error("{} - {}", linkMessage.getTitle(), JSONObject.toJSON(send));
            }
            return send;
        } catch (Exception e) {
            log.error("{} - {}", linkMessage.getTitle(), ExceptionUtils.getStackTrace(e));
            return SendResult.builder().success(false).build();
        }
    }

    // ---------------------- base ----------------------

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
    public static SendResult sendRobot(String webHook, String secret, Message message) throws IOException {
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
    public static SendResult sendChat(String accessToken, Message message) throws IOException {
        return execute(String.format(DingTalkClient.CHAT_URL, accessToken), message.toChatString());
    }

    private static SendResult execute(String url, String body) throws IOException {
        SendResult sendResult = new SendResult();
        HttpPost httppost = new HttpPost(url);
        httppost.addHeader("Content-Type", "application/json; charset=utf-8");
        httppost.setEntity(new StringEntity(body, "utf-8"));
        HttpResponse response = httpclient.execute(httppost);
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
    private static String secret(String webHook, String secret) {
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