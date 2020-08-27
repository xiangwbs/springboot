package com.xwbing.config.aliyun.oss.enums;

/**
 * 文件类型
 *
 * @author daofeng
 * @version $Id$
 * @since 2020年08月26日 11:52
 */
public enum ContentTypeEnum {
    //@formatter:off
    IMAGE(1,"图片"),
    HTML(2, "富文本内容"),
    FILE(3,"普通文件"),
    ;
    private final int code;

    private final String name;

    ContentTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
