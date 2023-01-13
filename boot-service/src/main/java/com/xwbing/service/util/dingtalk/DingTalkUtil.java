package com.xwbing.service.util.dingtalk;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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
import com.xwbing.service.demo.dingtalk.DingtalkHelper;

import lombok.extern.slf4j.Slf4j;

/**
 * @author xiangwb
 * @see DingtalkHelper
 */
@Deprecated
@Slf4j
public class DingTalkUtil {
    private static final HttpClient HTTP_CLIENT = HttpClients.createDefault();

    private DingTalkUtil() {
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
            SendResult send = sendRobot(DingTalkConstant.WEBHOK, DingTalkConstant.SECRET, linkMessage);
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
            SendResult send = sendRobot(DingTalkConstant.WEBHOK, DingTalkConstant.SECRET, textMessage);
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
            SendResult send = sendRobot(DingTalkConstant.WEBHOK, DingTalkConstant.SECRET, markdownMessage);
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
            webHook = DingtalkHelper.secret(webHook, secret);
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
        return execute(String.format(DingTalkConstant.CHAT_BASE_URL, accessToken), message.toChatString());
    }

    private static SendResult execute(String url, String body) throws IOException {
        SendResult sendResult = new SendResult();
        HttpPost httppost = new HttpPost(url);
        httppost.addHeader("Content-Type", "application/json; charset=utf-8");
        httppost.setEntity(new StringEntity(body, "utf-8"));
        HttpResponse response = HTTP_CLIENT.execute(httppost);
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
}