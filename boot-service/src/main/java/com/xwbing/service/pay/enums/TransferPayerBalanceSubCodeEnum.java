package com.xwbing.service.pay.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 付款方余额不足信息错误码
 *
 * @author daofeng
 * @version $
 * @since 2020年05月11日 10:33
 */
public enum TransferPayerBalanceSubCodeEnum {
    /** */
    PAYER_BALANCE_NOT_ENOUGH("PAYER_BALANCE_NOT_ENOUGH", "付款方余额不足"),
    BALANCE_IS_NOT_ENOUGH("BALANCE_IS_NOT_ENOUGH", "付款方余额不足"),
    ;

    private final String code;
    private final String desc;

    TransferPayerBalanceSubCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    private static final Map<String, TransferPayerBalanceSubCodeEnum> map = Arrays
            .stream(TransferPayerBalanceSubCodeEnum.values())
            .collect(Collectors.toMap(TransferPayerBalanceSubCodeEnum::getCode, Function.identity()));

    public static TransferPayerBalanceSubCodeEnum parse(String code) {
        return map.get(code);
    }

    public static List<String> listCode() {
        return Arrays.stream(TransferPayerBalanceSubCodeEnum.values()).map(TransferPayerBalanceSubCodeEnum::getCode)
                .collect(Collectors.toList());
    }
}
