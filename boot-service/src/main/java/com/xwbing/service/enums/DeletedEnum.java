package com.xwbing.service.enums;

import com.xwbing.service.enums.base.BaseEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 是否逻辑删除标识
 *
 * @author xiangwb
 */
@Getter
@AllArgsConstructor
public enum DeletedEnum implements BaseEnum {
    //@formatter:off
    YES(1, "已删除"),
    NO(0, "未删除"),
    //@formatter:on
    ;

    private final int code;
    private final String desc;
}
