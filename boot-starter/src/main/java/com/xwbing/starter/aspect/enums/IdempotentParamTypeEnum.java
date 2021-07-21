package com.xwbing.starter.aspect.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 接口幂等参数类型
 *
 * @author daofeng
 * @version $
 * @since 2021年02月07日 上午09:40
 */
@Getter
@AllArgsConstructor
public enum IdempotentParamTypeEnum {
    //@formatter:off
    HEADER( "请求头"),
    PARAM( "请求参数"),
    ;

    private final String name;
}
