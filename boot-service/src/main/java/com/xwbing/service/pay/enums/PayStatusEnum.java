package com.xwbing.service.pay.enums;

/**
 * @author daofeng
 * @version $
 * @since 2020年05月19日 19:33
 */
public enum PayStatusEnum {
    //@formatter:off
    PAYING("PAYING", "支付中"),
    SUCCESS("SUCCESS", "支付成功"),
    FAIL("FAIL", "支付失败"),
    CLOSED("CLOSED", "已退款"),;

    private final String code;
    private final String desc;

    PayStatusEnum(String code, String desc) {
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
