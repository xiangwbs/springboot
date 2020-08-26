package com.xwbing.service.service.pay.vo;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 交易预下单参数
 *
 * @author xwbing
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AliPayTradePreCreateParam {
    /**
     * 商户订单号
     */
    private String outTradeNo;
    /**
     * 订单标题
     */
    private String subject;
    /**
     * 总金额
     * 单位为元，精确到小数点后两位
     * 取值范围[0.01,100000000]
     */
    private BigDecimal totalAmount;
    private AliPayExtendParam extendParam;

    public static AliPayTradePreCreateParam of(String outTradeNo, String subject, BigDecimal totalAmount) {
        return AliPayTradePreCreateParam.builder().outTradeNo(outTradeNo).subject(subject).totalAmount(totalAmount)
                .build();
    }
}
