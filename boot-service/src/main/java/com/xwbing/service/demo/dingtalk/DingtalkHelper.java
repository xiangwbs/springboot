package com.xwbing.service.demo.dingtalk;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;

import com.alibaba.fastjson.JSONObject;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.taobao.api.ApiException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author daofeng
 * @version $Id$
 * @since 2023年01月09日 4:51 PM
 */
@Slf4j
public class DingtalkHelper {
    public static DingtalkRobotMsg receiveMsg(JSONObject msg) {
        log.info("receiveMsg msg:{}", msg);
        if (msg == null) {
            return null;
        }

        String msgId = msg.getString("msgId");
        String sessionWebhook = msg.getString("sessionWebhook");
        String senderStaffId = msg.getString("senderStaffId");
        String senderId = msg.getString("senderId");
        String senderNick = msg.getString("senderNick");
        Integer conversationType = msg.getInteger("conversationType");
        String createAt = msg.getString("createAt");
        String robotCode = msg.getString("robotCode");
        String content = Optional.ofNullable(msg.getJSONObject("text"))
                .map(text -> text.getString("content").replaceAll(" ", "")).orElse(null);

        return DingtalkRobotMsg.builder().client(new DefaultDingTalkClient(sessionWebhook)).senderStaffId(senderStaffId)
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
        StringBuilder textBuilder = at(request, atAll, userIds, "\n");
        text.setContent(textBuilder.append(content).toString());
        request.setText(text);
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
        StringBuilder textBuilder = at(request, atAll, userIds, "\n\n");
        markdown.setText(textBuilder.append(content).toString());
        request.setMarkdown(markdown);
        log.info("sendRobotMarkdown request:{}", JSONObject.toJSONString(request));
        try {
            OapiRobotSendResponse response = client.execute(request);
            log.info("sendRobotMarkdown response:{}", response.getBody());
        } catch (ApiException e) {
            log.error("sendRobotMarkdown error", e);
        }
    }

    /**
     * 整体跳转
     *
     * @param client
     * @param atAll
     * @param userIds
     * @param title
     * @param content markdown格式的消息
     * @param url
     */
    public static void sendActionCard(DingTalkClient client, boolean atAll, List<String> userIds, String title,
            String content, String url) {
        if (client == null) {
            return;
        }
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype("actionCard");
        OapiRobotSendRequest.Actioncard actionCard = new OapiRobotSendRequest.Actioncard();
        actionCard.setTitle(title);
        StringBuilder textBuilder = at(request, atAll, userIds, "\n\n");
        actionCard.setText(textBuilder.append(content).toString());
        actionCard.setSingleTitle("阅读全文");
        actionCard.setSingleURL(url);
        request.setActionCard(actionCard);
        try {
            log.info("sendActionCard request:{}", JSONObject.toJSONString(request));
            OapiRobotSendResponse response = client.execute(request);
            log.info("sendActionCard response:{}", response.getBody());
        } catch (ApiException e) {
            log.error("sendActionCard error", e);
        }
    }

    /**
     * 自动发送dtmd值并@机器人
     * dtmd协议只能在markdown、actioncard、feedcard消息类型中使用
     * 参考 企业内部机器人实现在单聊会话发送互动卡片案例
     *
     * @return
     */
    public static String dtmdLink(String content) {
        try {
            return "[" + content + "](dtmd://dingtalkclient/sendMessage?content=" + URLEncoder.encode(content, "UTF-8")
                    + ")";
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     * 消息内容content中要带上"@用户的userId"，跟atUserIds参数结合使用，才有@效果
     *
     * @param atAll
     * @param userIds
     * @param wrap
     *
     * @return
     */
    private static StringBuilder at(OapiRobotSendRequest request, boolean atAll, List<String> userIds, String wrap) {
        StringBuilder textBuilder = new StringBuilder();
        if (atAll) {
            textBuilder.append("@所有人").append(wrap);
        } else if (CollectionUtils.isNotEmpty(userIds)) {
            userIds.forEach(userId -> textBuilder.append("@").append(userId));
            textBuilder.append(wrap);
        }
        OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
        at.setAtUserIds(userIds);
        at.setIsAtAll(atAll);
        request.setAt(at);
        return textBuilder;
    }
}