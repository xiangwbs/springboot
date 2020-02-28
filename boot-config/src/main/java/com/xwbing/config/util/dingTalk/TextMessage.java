package com.xwbing.config.util.dingTalk;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xiangwb
 */
public class TextMessage implements Message {
    /**
     * 消息内容
     */
    private String text;
    /**
     * 被@人的手机号，号码必须正确，否则不起作用
     */
    private List<String> atMobiles = new ArrayList<>();
    /**
     * -@所有人时：true，否则为：false
     */
    private boolean isAtAll = false;

    public TextMessage(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void addAtMobile(String mobile) {
        if (StringUtils.isNotEmpty(mobile)) {
            this.atMobiles.add(mobile);
        }
    }

    public void addAtMobiles(List<String> mobiles) {
        if (CollectionUtils.isNotEmpty(mobiles)) {
            this.atMobiles.addAll(mobiles);
        }
    }

    public boolean isAtAll() {
        return this.isAtAll;
    }

    public void setAtAll(boolean atAll) {
        this.isAtAll = atAll;
    }

    public String toJsonString() {
        Map<String, Object> items = new HashMap<>();
        //msgtype
        items.put("msgtype", "text");
        //text
        Map<String, String> textContent = new HashMap<>();
        textContent.put("content", this.text);
        items.put("text", textContent);
        //at
        Map<String, Object> atItems = new HashMap<>();
        if (this.isAtAll) {
            atItems.put("isAtAll", true);
        } else if (!this.atMobiles.isEmpty()) {
            atItems.put("atMobiles", this.atMobiles);
        }
        items.put("at", atItems);
        return JSON.toJSONString(items);
    }
}
