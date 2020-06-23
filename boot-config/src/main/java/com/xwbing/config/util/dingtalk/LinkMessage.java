package com.xwbing.config.util.dingtalk;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xiangwb
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinkMessage implements Message {
    /**
     * 消息内容。如果太长只会部分展示
     */
    private String text;
    private String title;
    private String picUrl;
    private String messageUrl;
    private String chatId;

    @Override
    public String toRobotString() {
        Map<String, Object> items = new HashMap<>(2);
        items.put("msgtype", "link");
        Map<String, String> linkContent = new HashMap<>(4);
        linkContent.put("text", this.text);
        linkContent.put("title", this.title);
        linkContent.put("picUrl", this.picUrl);
        linkContent.put("messageUrl", this.messageUrl);
        items.put("link", linkContent);
        return JSON.toJSONString(items);
    }

    @Override
    public String toChatString() {
        Map<String, Object> content = new HashMap<>(2);
        content.put("chatid", chatId);
        String msg = this.toRobotString();
        content.put("msg", msg);
        return JSON.toJSONString(content);
    }
}
