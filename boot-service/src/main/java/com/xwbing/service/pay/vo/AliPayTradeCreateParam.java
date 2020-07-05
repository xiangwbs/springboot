package com.xwbing.service.pay.vo;

import java.math.BigDecimal;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一收单交易创建参数
 *
 * @author xwbing
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AliPayTradeCreateParam {
    /**
     * 商户订单号
     */
    @JSONField(name = "out_trade_no")
    private String outTradeNo;
    /**
     * 订单标题
     */
    private String subject;
    /**
     * 买家的支付宝唯一用户号
     */
    @JSONField(name = "buyer_id")
    private String buyerId;
    /**
     * 总金额
     * 单位为元，精确到小数点后两位
     * 取值范围[0.01,100000000]
     */
    @JSONField(name = "total_amount")
    private BigDecimal totalAmount;
    /**
     * 支付超时时间
     * 1m～15d。m-分钟，h-小时，d-天
     */
    @JSONField(name = "timeout_express")
    private String timeoutExpress;
    /**
     * hb_fq_num 花呗分期数 仅支持传入 3、6、12
     * hb_fq_seller_percent 卖家承担收费比例 商家承担手续费传入 100，用户承担手续费传入 0，仅支持传入 100、0 两种
     */
    @JSONField(name = "extend_params")
    private AliPayExtendParam extendParams;

    public static AliPayTradeCreateParam build(String outTradeNo, String buyerId, String subject,
            BigDecimal totalAmount, AliPayExtendParam extendParams) {
        return AliPayTradeCreateParam.builder().outTradeNo(outTradeNo).subject(subject).totalAmount(totalAmount)
                .buyerId(buyerId).extendParams(extendParams).timeoutExpress("10m").build();
    }
}
