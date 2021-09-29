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
public enum OrderStatusEnum implements BaseEnum {
    //@formatter:off
    WAIT(10, "待支付","待收款"),
    SUCCESS(20, "交易成功","交易成功"),
    CLOSE(30, "交易关闭","交易关闭"),
    ;
    //@formatter:on

    private final int code;
    private final String desc;
    private final String alias;


    private static final Map<Integer, OrderStatusEnum> ENUM_MAP = Arrays.stream(OrderStatusEnum.values())
            .collect(Collectors.toMap(OrderStatusEnum::getCode, Function.identity()));

    public static OrderStatusEnum parse(int code) {
        return ENUM_MAP.get(code);
    }
}
