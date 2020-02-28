package com.xwbing.domain.entity.pay.alipay;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 说明: 支付宝退款查询结果
 * 创建时间: 2017/5/10 17:38
 * 作者:  xiangwb
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AliPayRefundQueryResult extends AliPayBaseResult {
    /**
     * 退款原因
     */
    private String refundReason;
    /**
     * 退款时间
     */
    private Date refundTime;

    public AliPayRefundQueryResult(boolean success) {
        this.setSuccess(success);
    }
}

