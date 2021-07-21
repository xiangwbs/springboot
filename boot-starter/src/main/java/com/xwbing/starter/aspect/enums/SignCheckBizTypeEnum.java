package com.xwbing.starter.aspect.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 签名校验业务类型
 *
 * @author daofeng
 * @version $
 * @since 2021年02月07日 上午09:40
 */
@Getter
@AllArgsConstructor
public enum SignCheckBizTypeEnum {
    //@formatter:off
    DEFAULT("default", "默认"),
    ;

    private final String code;
    private final String name;

    private static final Map<String, SignCheckBizTypeEnum> ENUM_MAP = Arrays.stream(SignCheckBizTypeEnum.values())
            .collect(Collectors.toMap(SignCheckBizTypeEnum::getCode, Function.identity()));

    public static SignCheckBizTypeEnum parse(String code) {
        return ENUM_MAP.get(code);
    }
}
