package com.xwbing.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author daofeng
 * @version $Id$
 * @since 2020年06月19日 下午2:10
 */
public enum SexEnum {
    //@formatter:off
    MAN("男", "1"),
    WOMAN("女", "0"),
    ;
    private String name;
    private String code;

    SexEnum(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    private static final Map<String, String> ENUM_MAP = Arrays.stream(SexEnum.values())
            .collect(Collectors.toMap(SexEnum::getCode, SexEnum::getName));

    public static String parse(String code) {
        return ENUM_MAP.get(code);
    }
}
