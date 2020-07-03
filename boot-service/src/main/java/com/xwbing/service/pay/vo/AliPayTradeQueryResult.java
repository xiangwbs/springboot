package com.xwbing.service.pay.vo;

import com.alipay.api.response.AlipayTradeQueryResponse;
import com.xwbing.service.pay.enums.AliPayTradeStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 统一收单线下交易查询结果
 *
 * @author xwbing
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class AliPayTradeQueryResult extends AliPayBaseResult {
    /**
     * 退款支付时间
     */
    private AliPayTradeStatusEnum tradeStatus;

    public static AliPayTradeQueryResult ofSuccess(AlipayTradeQueryResponse response) {
        return AliPayTradeQueryResult.builder().success(true)
                .tradeStatus(AliPayTradeStatusEnum.parse(response.getTradeStatus())).message(response.getMsg())
                .code(response.getCode()).build();
    }

    public static AliPayTradeQueryResult ofFail(AlipayTradeQueryResponse response) {
        return AliPayTradeQueryResult.builder().success(false).message(response.getSubMsg()).code(response.getSubCode())
                .build();
    }

    public static AliPayTradeQueryResult ofError() {
        return AliPayTradeQueryResult.builder().success(false).code("unknow-error").message("服务暂不可用").build();
    }
}
