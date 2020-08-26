package com.xwbing.service.service.pay.vo;

import com.alipay.api.response.AlipayFundTransCommonQueryResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 转账查询结果
 *
 * @author xwbing
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class AliPayTransferQueryResult extends AliPayBaseResult {
    /**
     * 支付宝转账单据号，查询失败不返回。
     */
    private String orderId;
    /**
     * 商户订单号
     */
    private String outBizNo;
    /**
     * 支付时间
     */
    private String payDate;
    /**
     * 转账单据状态
     */
    private String status;

    public static AliPayTransferQueryResult ofSuccess(AlipayFundTransCommonQueryResponse response) {
        return AliPayTransferQueryResult.builder().success(true).message(response.getMsg()).code(response.getCode())
                .orderId(response.getOrderId()).outBizNo(response.getOutBizNo()).status(response.getStatus())
                .payDate(response.getPayDate()).build();
    }

    public static AliPayTransferQueryResult ofFail(AlipayFundTransCommonQueryResponse response) {
        return AliPayTransferQueryResult.builder().success(false).message(response.getFailReason())
                .code(response.getErrorCode()).build();
    }

    public static AliPayTransferQueryResult ofError() {
        return AliPayTransferQueryResult.builder().success(false).code("unknow-error").message("服务暂不可用").build();
    }
}
