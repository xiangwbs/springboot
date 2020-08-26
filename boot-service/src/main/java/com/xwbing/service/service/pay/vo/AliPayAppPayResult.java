package com.xwbing.service.service.pay.vo;

import com.alipay.api.response.AlipayTradeAppPayResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author xwbing
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class AliPayAppPayResult extends AliPayBaseResult {
    /**
     * orderString
     */
    private String body;

    public static AliPayAppPayResult ofSuccess(AlipayTradeAppPayResponse response) {
        return AliPayAppPayResult.builder().success(true).code(response.getCode()).message(response.getMsg())
                .body(response.getBody()).build();
    }

    public static AliPayAppPayResult ofFail(AlipayTradeAppPayResponse response) {
        return AliPayAppPayResult.builder().success(false).code(response.getSubCode()).message(response.getSubMsg())
                .build();
    }

    public static AliPayAppPayResult ofError() {
        return AliPayAppPayResult.builder().success(false).code("unknow-error").message("服务暂不可用").build();
    }

}