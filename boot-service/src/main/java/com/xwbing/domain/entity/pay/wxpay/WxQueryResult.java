package com.xwbing.domain.entity.pay.wxpay;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 说明: 微信查询状态
 * 项目名称: boot-module-pro
 * 创建时间: 2017/5/10 17:43
 * 作者:  xiangwb
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class WxQueryResult extends WxBaseResult {
    /**
     * 交易支付状态
     */
    private String tradeStatus;
    /**
     * 退款状态
     */
    private String refundStatus;

    public WxQueryResult(boolean success) {
        this.setSuccess(success);
    }
}
