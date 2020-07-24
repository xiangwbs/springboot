package com.xwbing.service.pay.vo;

import java.math.BigDecimal;

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
    private String outTradeNo;
    /**
     * 订单标题
     */
    private String subject;
    /**
     * 买家的支付宝唯一用户号
     */
    private String buyerId;
    /**
     * 总金额
     * 单位为元，精确到小数点后两位
     * 取值范围[0.01,100000000]
     */
    private BigDecimal totalAmount;
    // /**
    //  * hb_fq_num 花呗分期数 仅支持传入 3、6、12
    //  * hb_fq_seller_percent 卖家承担收费比例 商家承担手续费传入 100，用户承担手续费传入 0，仅支持传入 100、0 两种
    //  */
    // @JSONField(name = "extend_params")
    // private AliPayExtendParam extendParams;

    public static AliPayTradeCreateParam build(String outTradeNo, String buyerId, String subject,
            BigDecimal totalAmount) {
        return AliPayTradeCreateParam.builder().outTradeNo(outTradeNo).subject(subject).totalAmount(totalAmount)
                .buyerId(buyerId).build();
    }
}
