package com.xwbing.service.demo.dingtalk;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.alibaba.fastjson.JSONObject;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.taobao.api.ApiException;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author daofeng
 * @version $Id$
 * @since 2023年01月09日 4:51 PM
 */
@Slf4j
public class DingTalkHelper {
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RobotCallbackVO {
        private DefaultDingTalkClient client;
        // 企业内部群中@该机器人的成员userId
        private String senderStaffId;
        // 加密的发送者ID
        private String senderId;
        // 发送者昵称
        private String senderNick;
        private String content;
        // 1=单聊 2=群聊
        private Integer conversationType;
        private String msgId;
        // 消息的时间戳，单位ms
        private String createAt;
        // 机器人应用的AppKey
        private String robotCode;
    }

    public static RobotCallbackVO robotCallback(JSONObject msg) {
        log.info("robotCallback msg:{}", msg);
        if (msg == null) {
            return null;
        }

        String sessionWebhook = msg.getString("sessionWebhook");
        String senderStaffId = msg.getString("senderStaffId");
        String senderId = msg.getString("senderId");
        String senderNick = msg.getString("senderNick");
        String content = msg.getJSONObject("text").get("content").toString().replaceAll(" ", "");
        Integer conversationType = msg.getInteger("conversationType");
        String msgId = msg.getString("msgId");
        String createAt = msg.getString("createAt");
        String robotCode = msg.getString("robotCode");

        return RobotCallbackVO.builder().client(new DefaultDingTalkClient(sessionWebhook)).senderStaffId(senderStaffId)
                .senderId(senderId).senderNick(senderNick).content(content).conversationType(conversationType)
                .msgId(msgId).createAt(createAt).robotCode(robotCode).build();
    }

    public static void sendText(DingTalkClient client, boolean atAll, List<String> userIds, String content) {
        if (client == null) {
            return;
        }
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype("text");
        OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
        StringBuilder textBuilder = new StringBuilder();
        // 消息内容content中要带上"@用户的userId"，跟atUserIds参数结合使用，才有@效果。
        if (atAll) {
            textBuilder.append("@所有人").append("\n");
        } else if (CollectionUtils.isNotEmpty(userIds)) {
            userIds.forEach(userId -> textBuilder.append("@").append(userId));
            textBuilder.append("\n");
        }
        text.setContent(textBuilder.append(content).toString());
        request.setText(text);
        OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
        at.setAtUserIds(userIds);
        at.setIsAtAll(atAll);
        request.setAt(at);
        log.info("sendRobotText request:{}", JSONObject.toJSONString(request));
        try {
            OapiRobotSendResponse response = client.execute(request);
            log.info("sendRobotText response:{}", response.getBody());
        } catch (ApiException e) {
            log.error("sendRobotText error", e);
        }
    }

    public static void sendMarkdown(DingTalkClient client, boolean atAll, List<String> userIds, String title,
            DingMarkdown content) {
        if (client == null) {
            return;
        }
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype("markdown");
        OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
        markdown.setTitle(title);
        StringBuilder textBuilder = new StringBuilder();
        if (atAll) {
            textBuilder.append("@所有人").append("\n\n");
        } else if (CollectionUtils.isNotEmpty(userIds)) {
            userIds.forEach(userId -> textBuilder.append("@").append(userId));
            textBuilder.append("\n\n");
        }
        markdown.setText(textBuilder.append(content).toString());
        request.setMarkdown(markdown);
        OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
        at.setAtUserIds(userIds);
        at.setIsAtAll(atAll);
        request.setAt(at);
        log.info("sendRobotMarkdown request:{}", JSONObject.toJSONString(request));
        try {
            OapiRobotSendResponse response = client.execute(request);
            log.info("sendRobotMarkdown response:{}", response.getBody());
        } catch (ApiException e) {
            log.error("sendRobotMarkdown error", e);
        }
    }

    public static void sendActionCard(DingTalkClient client, boolean atAll, List<String> userIds, String title,
            String content, String url) {
        if (client == null) {
            return;
        }
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype("actionCard");
        OapiRobotSendRequest.Actioncard actionCard = new OapiRobotSendRequest.Actioncard();
        actionCard.setTitle(title);
        StringBuilder textBuilder = new StringBuilder();
        if (atAll) {
            textBuilder.append("@所有人").append("\n\n");
        } else if (CollectionUtils.isNotEmpty(userIds)) {
            userIds.forEach(userId -> textBuilder.append("@").append(userId));
            textBuilder.append("\n\n");
        }
        actionCard.setText(textBuilder.append(content).toString());
        actionCard.setSingleTitle("阅读全文");
        actionCard.setSingleURL(url);
        request.setActionCard(actionCard);
        OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
        at.setAtUserIds(userIds);
        at.setIsAtAll(atAll);
        request.setAt(at);
        try {
            log.info("sendActionCard request:{}", JSONObject.toJSONString(request));
            OapiRobotSendResponse response = client.execute(request);
            log.info("sendActionCard response:{}", response.getBody());
        } catch (ApiException e) {
            log.error("sendActionCard error", e);
        }
    }

    /**
     * 自动发送dtmd信息并@机器人
     * dtmd协议只能在markdown、actioncard、feedcard 消息类型中使用
     * 参考 企业内部机器人实现在单聊会话发送互动卡片案例
     *
     * @return
     */
    public static String dtmd(String content) {
        try {
            String encodeContent = URLEncoder.encode(content, "UTF-8");
            return "dtmd://dingtalkclient/sendMessage?content=" + encodeContent;
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }
}