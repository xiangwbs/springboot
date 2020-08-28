package com.xwbing.starter.alipay.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author xwbing
 */
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class AliPayBaseResult {
    /**
     * 是否成功
     */
    private boolean success;
    /**
     * 错误信息
     */
    private String message;
    /**
     * 错误码
     */
    private String code;
}
