package com.xwbing.domain.entity.pay.alipay;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 说明: 支付宝查询状态
 * 项目名称: boot-module-pro
 * 创建时间: 2017/5/10 17:38
 * 作者:  xiangwb
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AliPayQueryResult extends AliPayBaseResult {
    /**
     * 交易支付状态
     */
    private String tradeStatus;

    public AliPayQueryResult(boolean success) {
        this.setSuccess(success);
    }
}

