package com.xwbing.service.service.pay.enums;

/**
 * 支付宝业务类型
 *
 * @author daofeng
 * @version $Id$
 * @since 2020年06月19日 下午3:52
 */
public enum TradeTypeEnum {
    //@formatter:off
    transfer("transfer", "转账"),;

    private final String code;
    private final String name;

    TradeTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }
}
