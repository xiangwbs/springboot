package com.xwbing.service.pay.vo;

import java.math.BigDecimal;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 手机网站支付参数
 *
 * @author xwbing
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AliPayWapPayParam {
    /**
     * 商户订单号
     */
    @JSONField(name = "out_trade_no")
    private String outTradeNo;
    /**
     * 总金额
     * 单位为元，精确到小数点后两位
     * 取值范围[0.01,100000000]
     */
    @JSONField(name = "total_amount")
    private BigDecimal totalAmount;
    /**
     * 订单标题
     */
    private String subject;
    /**
     * 用户付款中途退出返回商户网站的地址
     */
    @JSONField(name = "quit_url")
    private String quitUrl;
    /**
     * 销售产品码，商家和支付宝签约的产品码
     * QUICK_WAP_WAY
     */
    @JSONField(name = "product_code")
    private String productCode = "QUICK_WAP_WAY";
    /**
     * 支付超时时间
     * 1m～15d。m-分钟，h-小时，d-天
     */
    @JSONField(name = "timeout_express")
    private String timeoutExpress;
    private transient String returnUrl;

    public static AliPayWapPayParam build(String outTradeNo, String subject, BigDecimal totalAmount, String returnUrl,
            String quitUrl) {
        return AliPayWapPayParam.builder().outTradeNo(outTradeNo).subject(subject).totalAmount(totalAmount)
                .returnUrl(returnUrl).quitUrl(quitUrl).productCode("QUICK_WAP_WAY").timeoutExpress("10m").build();
    }
}
