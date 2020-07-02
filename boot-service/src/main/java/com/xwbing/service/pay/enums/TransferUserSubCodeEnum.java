package com.xwbing.service.pay.enums;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 用户信息错误码
 *
 * @author daofeng
 * @version $
 * @since 2020年05月11日 10:33
 */
public enum TransferUserSubCodeEnum {
    //@formatter:off
    PAYEE_NOT_EXIST("PAYEE_NOT_EXIST", "收款用户不存在"),
    PAYEE_ACCOUNT_NOT_EXSIT("PAYEE_ACCOUNT_NOT_EXSIT", "收款账号不存在"),
    ;

    private final String code;
    private final String desc;

    TransferUserSubCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    private static final Map<String, TransferUserSubCodeEnum> map = Arrays.stream(TransferUserSubCodeEnum.values())
            .collect(Collectors.toMap(TransferUserSubCodeEnum::getCode, Function.identity()));

    public static TransferUserSubCodeEnum parse(String code) {
        return map.get(code);
    }
}
