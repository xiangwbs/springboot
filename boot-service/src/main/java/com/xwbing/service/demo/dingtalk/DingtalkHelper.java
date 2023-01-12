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
import com.dingtalk.api.request.OapiRobotSendRequest.Btns;
import com.dingtalk.api.request.OapiRobotSendRequest.Links;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.taobao.api.ApiException;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author daofeng
 * @version $Id$
 * @since 2023年01月09日 4:51 PM
 */
@Slf4j
public class DingtalkHelper {
    public static DingtalkRobotMsg receiveMsg(JSONObject msg) {
        log.info("receiveRobotMsg msg:{}", msg);
        if (msg == null) {
            return null;
        }
        DingtalkRobotMsg dingtalkRobotMsg = JSONUtil.toBean(msg.toJSONString(), DingtalkRobotMsg.class);
        String sessionWebhook = msg.getString("sessionWebhook");
        String content = Optional.ofNullable(msg.getJSONObject("text"))
                .map(text -> text.getString("content").replaceAll(" ", "")).orElse(null);
        return dingtalkRobotMsg.toBuilder().client(new DefaultDingTalkClient(sessionWebhook)).content(content).build();
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

    /**
     * @param client
     * @param title
     * @param content 消息内容。如果太长只会部分展示
     * @param messageUrl 点击消息跳转的URL
     * @param picUrl
     */
    public static void sendLink(DingTalkClient client, String title, String content, String messageUrl, String picUrl) {
        if (client == null) {
            return;
        }
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype("link");
        OapiRobotSendRequest.Link link = new OapiRobotSendRequest.Link();
        link.setTitle(title);
        link.setText(content);
        link.setMessageUrl(messageUrl);
        link.setPicUrl(picUrl);
        request.setLink(link);
        log.info("sendRobotLink request:{}", JSONObject.toJSONString(request));
        try {
            OapiRobotSendResponse response = client.execute(request);
            log.info("sendRobotLink response:{}", response.getBody());
        } catch (ApiException e) {
            log.error("sendRobotLink error", e);
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
     * 整体跳转action
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
     * 独立跳转action
     *
     * @param client
     * @param atAll
     * @param userIds
     * @param title
     * @param content
     * @param btnOrientation 0-按钮竖直排列，1-按钮横向排列
     * @param btns
     */
    public static void sendActionCard(DingTalkClient client, boolean atAll, List<String> userIds, String title,
            String content, String btnOrientation, List<Btns> btns) {
        if (client == null) {
            return;
        }
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype("actionCard");
        OapiRobotSendRequest.Actioncard actionCard = new OapiRobotSendRequest.Actioncard();
        actionCard.setTitle(title);
        StringBuilder textBuilder = at(request, atAll, userIds, "\n\n");
        actionCard.setText(textBuilder.append(content).toString());
        actionCard.setBtnOrientation(btnOrientation);
        actionCard.setBtns(btns);
        request.setActionCard(actionCard);
        try {
            log.info("sendActionCard request:{}", JSONObject.toJSONString(request));
            OapiRobotSendResponse response = client.execute(request);
            log.info("sendActionCard response:{}", response.getBody());
        } catch (ApiException e) {
            log.error("sendActionCard error", e);
        }
    }

    public static void sendFeedCard(DingTalkClient client, List<Links> links) {
        if (client == null) {
            return;
        }
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype("feedCard");
        OapiRobotSendRequest.Feedcard feedCard = new OapiRobotSendRequest.Feedcard();
        feedCard.setLinks(links);
        request.setFeedCard(feedCard);
        try {
            log.info("sendFeedCard request:{}", JSONObject.toJSONString(request));
            OapiRobotSendResponse response = client.execute(request);
            log.info("sendFeedCard response:{}", response.getBody());
        } catch (ApiException e) {
            log.error("sendFeedCard error", e);
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