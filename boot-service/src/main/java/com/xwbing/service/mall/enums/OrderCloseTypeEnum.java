package com.xwbing.service.mall.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.xwbing.service.enums.base.BaseEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 订单状态
 *
 * @author daofeng
 * @version $Id$
 * @since 2020年07月08日 下午3:06
 */
@Getter
@AllArgsConstructor
public enum OrderCloseTypeEnum implements BaseEnum {
    //@formatter:off
    CLOSE(10, "关闭"),
    TIMEOUT(20, "超时"),
    REFUND(30, "退款"),
    UN_KNOW(99, "交易关闭"),
    ;
    //@formatter:on

    private final int code;
    private final String desc;


    private static final Map<Integer, OrderCloseTypeEnum> ENUM_MAP = Arrays.stream(OrderCloseTypeEnum.values())
            .collect(Collectors.toMap(OrderCloseTypeEnum::getCode, Function.identity()));

    public static OrderCloseTypeEnum parse(int code) {
        return ENUM_MAP.get(code);
    }
}
