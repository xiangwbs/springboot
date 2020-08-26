package com.xwbing.service.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author daofeng
 * @version $Id$
 * @since 2020年06月19日 下午2:11
 */
public enum LoginInOutEnum {
    //@formatter:off
    IN("登录", 1),
    OUT("登出", 2),
    ;
    private String name;
    private int value;

    LoginInOutEnum(String name, int value) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    private static final Map<Integer, String> ENUM_MAP = Arrays.stream(LoginInOutEnum.values())
            .collect(Collectors.toMap(LoginInOutEnum::getValue,LoginInOutEnum::getName));

    public static String parse(int value) {
        return ENUM_MAP.get(value);
    }
}
