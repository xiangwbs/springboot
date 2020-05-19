package com.xwbing.service.pay.enums;

/**
 * @author LiuGong
 * @version $
 * @since 2020年01月09日 19:33
 */
public enum TransferStatusEnum  {
    /** */
    SUCCESS("SUCCESS", "成功"),
    WAIT_PAY("WAIT_PAY", "等待支付"),
    CLOSED("CLOSED", "订单超时关闭"),
    FAIL("FAIL", "失败"),
    ;

    private final String code;
    private final String desc;

    TransferStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
