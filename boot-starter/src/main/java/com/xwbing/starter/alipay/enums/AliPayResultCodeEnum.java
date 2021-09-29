package com.xwbing.starter.alipay.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author daofeng
 * @version $
 * @since 2020年11月02日 下午14:58
 */
@Getter
@AllArgsConstructor
public enum AliPayResultCodeEnum {

    SUCCESS("SUCCESS"), FAIL("FAIL");

    private final String value;
}
