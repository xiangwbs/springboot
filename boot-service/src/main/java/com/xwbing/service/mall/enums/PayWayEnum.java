package com.xwbing.service.mall.enums;

import java.util.Arrays;
import java.util.Optional;

import com.xwbing.service.enums.base.BaseEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 支付方式
 *
 * @author daofeng
 * @version $
 * @since 2020年07月24日 下午15:22
 */
@Getter
@AllArgsConstructor
public enum PayWayEnum implements BaseEnum {
    MOBILE(10, "手机网站支付"),
    PC(20, "电脑网站支付"),
    APP(30, "app支付"),
    SCAN_CODE(40, "扫码支付"),
    AUTH_CODE(50, "付款码支付"),
    MINI_PROGRAM(60, "小程序支付"),
    ;

    private final int code;

    private final String name;

    public static PayWayEnum getByCode(int code) {
        Optional<PayWayEnum> optional = Arrays.stream(PayWayEnum.values()).filter(i -> i.getCode() == code).findFirst();
        return optional.orElse(null);
    }

}
