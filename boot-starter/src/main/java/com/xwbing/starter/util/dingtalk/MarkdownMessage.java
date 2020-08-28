package com.xwbing.starter.util.dingtalk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;

/**
 * @author xiangwb
 */
public class MarkdownMessage implements Message {
    /**
     * 首屏会话透出的展示内容
     */
    private String title;
    /**
     * 群会话的id 发送群消息时使用
     */
    private String chatId;
    /**
     * -@所有人时：true，否则为：false
     */
    private boolean isAtAll = false;
    /**
     * 被@人的手机号(在text内容里要有@手机号)
     */
    private List<String> atMobiles = new ArrayList<>();
    /**
     * markdown格式的消息
     */
    private List<String> items = new ArrayList<>();

    public MarkdownMessage() {
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public boolean isAtAll() {
        return this.isAtAll;
    }

    public void setAtAll(boolean atAll) {
        this.isAtAll = atAll;
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

    public void addItem(String text) {
        if (StringUtils.isNotEmpty(text)) {
            this.items.add(text);
        }
    }

    public void addItem(int index, String text) {
        boolean rangeCheck = index >= 0 && index <= this.items.size();
        if (rangeCheck && StringUtils.isNotEmpty(text)) {
            this.items.add(index, text);
        }
    }

    /**
     * 标题，总共六级
     *
     * @param headerType
     * @param text
     *
     * @return
     */
    public static String getHeaderText(int headerType, String text) {
        if (headerType >= 1 && headerType <= 6) {
            StringBuilder numbers = new StringBuilder();
            for (int i = 1; i <= headerType; i++) {
                numbers.append("#");
            }
            return numbers + " " + text;
        } else {
            throw new IllegalArgumentException("headerType should be in [1, 6]");
        }
    }

    /**
     * 引用
     *
     * @param text
     *
     * @return
     */
    public static String getReferenceText(String text) {
        return "> " + text;
    }

    /**
     * 加粗
     *
     * @param text
     *
     * @return
     */
    public static String getBoldText(String text) {
        return "**" + text + "**";
    }

    /**
     * 斜体
     *
     * @param text
     *
     * @return
     */
    public static String getItalicText(String text) {
        return "*" + text + "*";
    }

    /**
     * 链接
     *
     * @param text
     * @param href
     *
     * @return
     */
    public static String getLinkText(String text, String href) {
        return "[" + text + "](" + href + ")";
    }

    /**
     * 图片
     *
     * @param imageUrl
     *
     * @return
     */
    public static String getImageText(String imageUrl) {
        return "![image](" + imageUrl + ")";
    }

    /**
     * 有序列表
     *
     * @param orderItem
     *
     * @return
     */
    public static String getOrderListText(List<String> orderItem) {
        if (orderItem.isEmpty()) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i <= orderItem.size(); i++) {
                sb.append(i).append(". ").append(orderItem.get(i - 1)).append("\n");
            }
            return sb.toString();
        }
    }

    /**
     * 无序列表
     *
     * @param unOrderItem
     *
     * @return
     */
    public static String getUnOrderListText(List<String> unOrderItem) {
        if (unOrderItem.isEmpty()) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();
            for (String anUnOrderItem : unOrderItem) {
                sb.append("- ").append(anUnOrderItem).append("\n");
            }
            return sb.toString();
        }
    }

    @Override
    public String toRobotString() {
        Map<String, Object> items = new HashMap<>(3);
        //msgtype
        items.put("msgtype", "markdown");
        //markdown
        Map<String, Object> markdown = new HashMap<>(2);
        markdown.put("title", this.title);
        StringBuilder markdownText = new StringBuilder();
        for (Object item : this.items) {
            markdownText.append(item).append("\n\n");
        }
        //at(text添加@信息，否则@不起作用)
        Map<String, Object> atItems = new HashMap<>(1);
        if (this.isAtAll) {
            atItems.put("isAtAll", true);
            markdownText.append("@所有人");
        } else if (!this.atMobiles.isEmpty()) {
            atItems.put("atMobiles", this.atMobiles);
            atMobiles.forEach(mobile -> markdownText.append("@").append(mobile).append("\n"));
        }
        items.put("at", atItems);
        markdown.put("text", markdownText.toString());
        items.put("markdown", markdown);
        return JSON.toJSONString(items);
    }

    @Override
    public String toChatString() {
        Map<String, Object> content = new HashMap<>(2);
        content.put("chatid", chatId);
        Map<String, Object> markdown = new HashMap<>(2);
        markdown.put("title", this.title);
        StringBuilder markdownText = new StringBuilder();
        for (Object item : this.items) {
            markdownText.append(item).append("\n\n");
        }
        markdown.put("text", markdownText.toString());
        Map<String, Object> items = new HashMap<>(2);
        items.put("msgtype", "markdown");
        items.put("markdown", markdown);
        content.put("msg", JSON.toJSONString(items));
        return JSON.toJSONString(content);
    }
}