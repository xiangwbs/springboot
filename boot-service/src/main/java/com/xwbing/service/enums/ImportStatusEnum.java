package com.xwbing.service.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author daofeng
 * @version $Id$
 * @since 2020年04月28日 上午10:25
 */
public enum ImportStatusEnum {
    //@formatter:off
    EXPORT("正在导入中", "export"),
    FAIL("失败", "fail"),
    SUCCESS("成功", "success"),
    ;
    private String name;
    private String code;

    ImportStatusEnum(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    private static final Map<String, ImportStatusEnum> ENUM_MAP = Arrays.stream(ImportStatusEnum.values())
            .collect(Collectors.toMap(ImportStatusEnum::getCode, Function.identity()));

    public static ImportStatusEnum parse(String code) {
        return ENUM_MAP.get(code);
    }
}
