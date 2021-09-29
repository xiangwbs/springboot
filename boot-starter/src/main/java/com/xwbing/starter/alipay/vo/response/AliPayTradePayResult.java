package com.xwbing.starter.alipay.vo.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.alipay.api.domain.TradeFundBill;
import com.alipay.api.response.AlipayTradePayResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 统一收单交易支付结果
 *
 * @author xwbing
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class AliPayTradePayResult extends AliPayBaseResult {
    /**
     * 商户订单号
     */
    private String tradeNo;
    /**
     * 支付宝交易号
     */
    private String outTradeNo;
    /**
     * 买家支付宝账号
     */
    private String buyerLogonId;
    /**
     * 买家在支付宝的用户id
     */
    private String buyerUserId;
    /**
     * 买家用户类型。CORPORATE:企业用户；PRIVATE:个人用户。
     */
    private String buyerUserType;
    /**
     * 交易金额
     */
    private BigDecimal totalAmount;
    /**
     * 实收金额
     */
    private BigDecimal receiptAmount;
    /**
     * 商家优惠金额
     */
    private BigDecimal mdiscountAmount;
    /**
     * 平台优惠金额
     */
    private BigDecimal discountAmount;
    /**
     * 交易支付时间
     */
    private LocalDateTime gmtPayment;
    /**
     * 交易支付使用的资金渠道
     */
    private List<TradeFundBill> fundBillList;

    public static AliPayTradePayResult ofSuccess(AlipayTradePayResponse response) {
        //@formatter:off
        return AliPayTradePayResult
                .builder()
                .success(true)
                .code(response.getCode())
                .message(response.getMsg())
                .tradeNo(response.getOutTradeNo())
                .outTradeNo(response.getTradeNo())
                .buyerLogonId(response.getBuyerLogonId())
                .buyerUserId(response.getBuyerUserId())
                .buyerUserType(response.getBuyerUserType())
                .totalAmount(new BigDecimal(response.getTotalAmount()))
                .receiptAmount(new BigDecimal(response.getReceiptAmount()))
                .mdiscountAmount(StringUtils.isNotEmpty(response.getMdiscountAmount()) ? new BigDecimal(response.getMdiscountAmount()) : null)
                .discountAmount(StringUtils.isNotEmpty(response.getDiscountAmount()) ? new BigDecimal(response.getDiscountAmount()) : null)
                .gmtPayment(response.getGmtPayment() != null ? LocalDateTime.ofInstant(response.getGmtPayment().toInstant(), ZoneId.systemDefault()) : null)
                .fundBillList(response.getFundBillList())
                .build();
    }

    public static AliPayTradePayResult ofFail(AlipayTradePayResponse response) {
        return AliPayTradePayResult.builder().success(false).code(response.getSubCode()).message(response.getSubMsg())
                .build();
    }

    public static AliPayTradePayResult ofError() {
        return AliPayTradePayResult.builder().success(false).code("unknow-error").message("服务暂不可用").build();
    }
}