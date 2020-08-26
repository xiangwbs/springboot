package com.xwbing.service.service.pay.enums;

/**
 * @author daofeng
 * @version $
 * @since 2020年05月19日 19:33
 */
public enum PayTypeEnum {
    //@formatter:off
    WX("wx", "微信支付"),
    ALIPAY("alipay", "支付宝支付"),;

    private final String code;
    private final String desc;

    PayTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public String getCode() {
        return code;
    }
}
