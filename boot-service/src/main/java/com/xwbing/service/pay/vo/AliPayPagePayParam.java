package com.xwbing.service.pay.vo;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 电脑网站支付参数
 *
 * @author xwbing
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AliPayPagePayParam {
    /**
     * 商户订单号
     */
    private String outTradeNo;
    /**
     * 总金额
     * 单位为元，精确到小数点后两位
     * 取值范围[0.01,100000000]
     */
    private BigDecimal totalAmount;
    /**
     * 订单标题
     */
    private String subject;
    /**
     * 页面跳转同步通知页面地址
     */
    private String returnUrl;
    private AliPayExtendParam extendParam;

    public static AliPayPagePayParam build(String outTradeNo, String subject, BigDecimal totalAmount,
            String returnUrl) {
        return AliPayPagePayParam.builder().outTradeNo(outTradeNo).subject(subject).totalAmount(totalAmount)
                .returnUrl(returnUrl).build();
    }
}
