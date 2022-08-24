package com.xwbing.service.demo.es.user;

import com.xwbing.service.enums.base.BaseEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author daofeng
 * @version $
 * @since 2022/6/30 09:52
 */
@Getter
@AllArgsConstructor
public enum SearchRelationTypeEnum implements BaseEnum {
    //@formatter:off
    MUST(10, "是"),
    MUST_NOT(11, "不是"),
    SHOULD(20, "或"),
    BEFORE(30, "早于"),
    AFTER(31, "晚于"),
    ;
    //@formatter:on

    private final int code;
    private final String desc;
}
