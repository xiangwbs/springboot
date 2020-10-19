package com.xwbing.service.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author daofeng
 * @version $Id$
 * @since 2020年04月28日 上午10:25
 */
@Getter
@AllArgsConstructor
public enum ImportStatusEnum implements BaseEnum {
    //@formatter:off
    EXPORT( 1,"正在导入中"),
    FAIL(2,"失败"),
    SUCCESS( 3,"成功"),
    ;

    @Override
    public String toString() {
        return getName();
    }

    private int code;
    private String name;

    private static final Map<Integer, ImportStatusEnum> ENUM_MAP = Arrays.stream(ImportStatusEnum.values())
            .collect(Collectors.toMap(ImportStatusEnum::getCode, Function.identity()));

    public static ImportStatusEnum parse(int code) {
        return ENUM_MAP.get(code);
    }
}
