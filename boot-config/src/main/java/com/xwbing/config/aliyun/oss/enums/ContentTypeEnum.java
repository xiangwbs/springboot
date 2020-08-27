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
    IMAGE("image","图片"),
    HTML("html", "富文本内容"),
    FILE("file","普通文件"),
    ;
    private final String code;

    private final String name;

    ContentTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
