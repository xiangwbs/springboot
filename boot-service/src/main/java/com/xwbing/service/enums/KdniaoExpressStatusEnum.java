package com.xwbing.service.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 快递鸟物流状态
 *
 * @author daofeng
 * @version $Id$
 * @since 2020年06月19日 下午2:12
 */
public enum KdniaoExpressStatusEnum {
    //@formatter:off
    NO_MSG("无信息", 0),
    HAS_TAKE("已取件", 1),
    ON_THE_WAY("在途中", 2),
    RECEIVED("已签收", 3),
    QUESTION("问题件", 4),
    TO_TAKE("待取件", 5),
    TO_SEND("待派件", 6),
    HAS_SHIPPED("已发货", 8),
    UN_SHIPPED("未发货", 9),
    ;
    private String name;
    private int value;

    KdniaoExpressStatusEnum(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    private static final Map<Integer, String> ENUM_MAP = Arrays.stream(KdniaoExpressStatusEnum.values())
            .collect(Collectors.toMap(KdniaoExpressStatusEnum::getValue, KdniaoExpressStatusEnum::getName));

    public static String parse(int value) {
        return ENUM_MAP.get(value);
    }
}
