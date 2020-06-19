package com.xwbing.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author daofeng
 * @version $Id$
 * @since 2020年06月19日 下午2:11
 */
public enum MenuOrButtonEnum {
    //@formatter:off
    MENU("菜单", 1),
    BUTTON("按钮", 2),
    ;
    private int code;
    private String name;

    MenuOrButtonEnum(String name, int code) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    private static final Map<Integer, String> ENUM_MAP = Arrays.stream(MenuOrButtonEnum.values())
            .collect(Collectors.toMap(MenuOrButtonEnum::getCode,MenuOrButtonEnum::getName));

    public static String parse(int code) {
        return ENUM_MAP.get(code);
    }
}
