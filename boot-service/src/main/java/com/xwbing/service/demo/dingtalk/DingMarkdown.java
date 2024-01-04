package com.xwbing.service.demo.dingtalk;

import java.util.List;

/**
 * @author daofeng
 * @version $Id$
 * @since 2023年01月09日 4:51 PM
 */
public class DingMarkdown {
    private final StringBuilder content = new StringBuilder();

    @Override
    public String toString() {
        return content.toString();
    }

    /**
     * 构造钉钉 md 对象
     */
    public static DingMarkdown build() {
        return new DingMarkdown();
    }

    /**
     * 换行
     */
    public DingMarkdown newLine() {
        this.content.append("\n\n");
        return this;
    }

    /**
     * 追加标题，总共六级【1-6】
     *
     * @param headerLevel 标题级别
     * @param headerText 标题内容
     */
    public DingMarkdown appendHeader(int headerLevel, String headerText) {
        // 标题级别范围
        int headerLevelMin = 1;
        int headerLevelMax = 6;
        if (headerLevel >= headerLevelMin && headerLevel <= headerLevelMax) {
            for (int i = 1; i <= headerLevel; i++) {
                this.content.append("#");
            }
            this.content.append(" ").append(headerText).append("\n\n");
            return this;
        } else {
            throw new IllegalArgumentException("headerLevel should be in [1, 6]");
        }
    }

    /**
     * 追加普通文本
     *
     * @param text 文本内容
     */
    public DingMarkdown appendText(String text) {
        this.content.append(text);
        return this;
    }

    /**
     * 追加斜体字
     *
     * @param text 文本内容
     */
    public DingMarkdown appendItalicText(String text) {
        this.content.append("*").append(text).append("*");
        return this;
    }

    /**
     * 追加加粗
     *
     * @param text 文本内容
     */
    public DingMarkdown appendBoldText(String text) {
        this.content.append("**").append(text).append("**");
        return this;
    }

    /**
     * 追加引用
     *
     * @param text 文本内容
     */
    public DingMarkdown appendReference(String text) {
        this.content.append("> ").append(text);
        return this;
    }

    /**
     * 追加链接
     *
     * @param text 文本
     * @param href 链接地址
     */
    public DingMarkdown appendLink(String text, String href) {
        this.content.append("[").append(text).append("](").append(href).append(")");
        return this;
    }

    /**
     * 追加图片
     *
     * @param imageUrl 图片地址
     */
    public DingMarkdown appendImage(String imageUrl) {
        this.content.append("![](").append(imageUrl).append(")");
        return this;
    }

    /**
     * 追加有序列表
     *
     * @param list 内容列表
     */
    public DingMarkdown appendOrderedList(List<String> list) {
        for (int i = 0; i < list.size(); i++) {
            this.content.append(i + 1).append(". ").append(list.get(i)).append("\n");
        }
        return this;
    }

    /**
     * 追加无序列表
     *
     * @param list 内容列表
     */
    public DingMarkdown appendUnOrderedList(List<String> list) {
        list.forEach(item -> this.content.append("- ").append(item).append("\n"));
        return this;
    }
}