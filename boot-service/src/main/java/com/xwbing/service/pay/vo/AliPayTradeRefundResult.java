package com.xwbing.service.pay.vo;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 说明: 支付宝退款结果
 * 项目名称: boot-module-pro
 * 创建时间: 2017/5/10 17:39
 *
 * @author xwbing
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class AliPayTradeRefundResult extends AliPayBaseResult {
    /**
     * 退款支付时间
     */
    private Date refundTime;
}
