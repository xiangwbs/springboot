package com.xwbing.service.demo.dingtalk;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.collections.CollectionUtils;

import com.alibaba.fastjson.JSONObject;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.request.OapiRobotSendRequest.Btns;
import com.dingtalk.api.request.OapiRobotSendRequest.Links;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.taobao.api.ApiException;
import com.xwbing.service.util.dingtalk.DingTalkConstant;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author daofeng
 * @version $Id$
 * @since 2023年01月09日 4:51 PM
 */
@Slf4j
public class DingtalkHelper {
    public static DingtalkRobotMsg receiveMsg(JSONObject msg, String timestamp, String sign) {
        log.info("dingtalkRobot receiveMsg:{}", msg);
        if (msg == null) {
            return null;
        }
        //TODO 签名校验
        DingtalkRobotMsg dingtalkRobotMsg = JSONUtil.toBean(msg.toJSONString(), DingtalkRobotMsg.class);
        String sessionWebhook = msg.getString("sessionWebhook");
        String content = Optional.ofNullable(msg.getJSONObject("text"))
                .map(text -> text.getString("content").replaceAll(" ", "")).orElse(null);
        return dingtalkRobotMsg.toBuilder().client(new DefaultDingTalkClient(sessionWebhook)).content(content).build();
    }

    /**
     * 发送文本消息 @信息会自动拼接在最后面并高亮 @用户会换行 @所有人不会换行
     *
     * @param client
     * @param atAll
     * @param users userId/mobile
     * @param content
     */
    public static void sendText(DingTalkClient client, boolean atAll, List<String> users, String content) {
        if (client == null) {
            return;
        }
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype("text");
        OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
        // 跟@用户换行保持一致
        if (atAll) {
            content = content + "\n";
        }
        text.setContent(content);
        request.setText(text);
        OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
        at.setAtMobiles(users);
        at.setAtUserIds(users);
        at.setIsAtAll(atAll);
        request.setAt(at);
        log.info("dingtalkRobot sendText request:{}", JSONObject.toJSONString(request));
        try {
            OapiRobotSendResponse response = client.execute(request);
            log.info("dingtalkRobot sendText response:{}", response.getBody());
        } catch (ApiException e) {
            log.error("dingtalkRobot sendText error", e);
        }
    }

    /**
     * @param client
     * @param title
     * @param content 消息内容 如果太长只会部分展示
     * @param messageUrl 点击消息跳转的url
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
        log.info("dingtalkRobot sendLink request:{}", JSONObject.toJSONString(request));
        try {
            OapiRobotSendResponse response = client.execute(request);
            log.info("dingtalkRobot sendLink response:{}", response.getBody());
        } catch (ApiException e) {
            log.error("dingtalkRobot sendLink error", e);
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
        StringBuilder textBuilder = markdownAt(request, atAll, userIds);
        markdown.setText(textBuilder.append(content).toString());
        request.setMarkdown(markdown);
        log.info("dingtalkRobot sendMarkdown request:{}", JSONObject.toJSONString(request));
        try {
            OapiRobotSendResponse response = client.execute(request);
            log.info("dingtalkRobot sendMarkdown response:{}", response.getBody());
        } catch (ApiException e) {
            log.error("dingtalkRobot sendMarkdown error", e);
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
        StringBuilder textBuilder = markdownAt(request, atAll, userIds);
        actionCard.setText(textBuilder.append(content).toString());
        actionCard.setSingleTitle("阅读全文");
        actionCard.setSingleURL(url);
        request.setActionCard(actionCard);
        try {
            log.info("dingtalkRobot sendActionCard request:{}", JSONObject.toJSONString(request));
            OapiRobotSendResponse response = client.execute(request);
            log.info("dingtalkRobot sendActionCard response:{}", response.getBody());
        } catch (ApiException e) {
            log.error("dingtalkRobot sendActionCard error", e);
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
            String content, Integer btnOrientation, List<Btns> btns) {
        if (client == null) {
            return;
        }
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype("actionCard");
        OapiRobotSendRequest.Actioncard actionCard = new OapiRobotSendRequest.Actioncard();
        actionCard.setTitle(title);
        StringBuilder textBuilder = markdownAt(request, atAll, userIds);
        actionCard.setText(textBuilder.append(content).toString());
        actionCard.setBtnOrientation(String.valueOf(btnOrientation));
        actionCard.setBtns(btns);
        request.setActionCard(actionCard);
        try {
            log.info("dingtalkRobot sendActionCard request:{}", JSONObject.toJSONString(request));
            OapiRobotSendResponse response = client.execute(request);
            log.info("dingtalkRobot sendActionCard response:{}", response.getBody());
        } catch (ApiException e) {
            log.error("dingtalkRobot sendActionCard error", e);
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
            log.info("dingtalkRobot sendFeedCard request:{}", JSONObject.toJSONString(request));
            OapiRobotSendResponse response = client.execute(request);
            log.info("dingtalkRobot sendFeedCard response:{}", response.getBody());
        } catch (ApiException e) {
            log.error("dingtalkRobot sendFeedCard error", e);
        }
    }

    /**
     * 消息内容content中要带上"@用户的userId"，跟atUserIds参数结合使用，才有@效果
     *
     * @param atAll
     * @param users userId/mobile
     *
     * @return
     */
    private static StringBuilder markdownAt(OapiRobotSendRequest request, boolean atAll, List<String> users) {
        StringBuilder textBuilder = new StringBuilder();
        if (atAll) {
            textBuilder.append("@所有人").append("\n\n");
        } else if (CollectionUtils.isNotEmpty(users)) {
            users.forEach(userId -> textBuilder.append("@").append(userId));
            textBuilder.append("\n\n");
        }
        OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
        at.setAtMobiles(users);
        at.setAtUserIds(users);
        at.setIsAtAll(atAll);
        request.setAt(at);
        return textBuilder;
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
     * 签名
     *
     * @param timestamp
     * @param accessSecret
     *
     * @return
     */
    public static String sign(String timestamp, String accessSecret) {
        try {
            String stringToSign = timestamp + "\n" + accessSecret;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(accessSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(signData);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 签名校验
     *
     * @param sign
     * @param accessSecret
     * @param timestamp
     *
     * @return
     */
    public static boolean checkSign(String sign, String accessSecret, String timestamp) {
        String mySign = sign(timestamp, accessSecret);
        if (!mySign.equals(sign)) {
            return false;
        }
        Instant instant = Instant.ofEpochMilli(Long.valueOf(timestamp));
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return LocalDateTime.now().minusHours(1).isBefore(dateTime);
    }

    /**
     * 钉钉安全设置:加签
     *
     * @return webHook
     */
    public static String secret(String webHook, String accessSecret) {
        try {
            String timestamp = String.valueOf(System.currentTimeMillis());
            String sign = sign(timestamp, accessSecret);
            return String.format("%s&timestamp=%s&sign=%s", webHook, timestamp, URLEncoder.encode(sign, "UTF-8"));
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * pc端链接跳转
     * true：表示在PC客户端侧边栏打开
     * false：表示在浏览器打开
     *
     * @param linkUrl
     *
     * @return
     */
    public static String pcSlide(String linkUrl, boolean pcSlide) {
        try {
            return "dingtalk://dingtalkclient/page/link?pc_slide=" + pcSlide + "&url=" + URLEncoder
                    .encode(linkUrl, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            return linkUrl;
        }
    }

    public static void main(String[] args) {
        DefaultDingTalkClient client = new DefaultDingTalkClient(
                secret(DingTalkConstant.WEBHOK, DingTalkConstant.SECRET));
        sendText(client, false, Collections.singletonList("13456854170"), "测试文本");
        sendMarkdown(client, false, Collections.singletonList("13456854170"), "测试markdown文本",
                DingMarkdown.build().appendText("测试markdown文本"));
    }
}