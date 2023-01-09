package com.xwbing.service.demo.dingtalk;

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
        private String userId;
        private String content;
    }

    public static RobotCallbackVO robotCallback(JSONObject msg) {
        log.info("robotCallback msg:{}", msg);
        if (msg == null) {
            return RobotCallbackVO.builder().build();
        }
        String content = msg.getJSONObject("text").get("content").toString().replaceAll(" ", "");
        //获取用户userId
        String userId = msg.getString("senderStaffId");
        String sessionWebhook = msg.getString("sessionWebhook");
        return RobotCallbackVO.builder().client(new DefaultDingTalkClient(sessionWebhook)).userId(userId)
                .content(content).build();
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
}