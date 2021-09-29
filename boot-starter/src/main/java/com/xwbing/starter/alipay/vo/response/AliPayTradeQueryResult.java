package com.xwbing.starter.alipay.vo.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.apache.commons.lang3.StringUtils;

import com.alipay.api.response.AlipayTradeQueryResponse;
import com.xwbing.starter.alipay.enums.AliPayTradeStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 统一收单线下交易查询结果
 *
 * @author xwbing
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class AliPayTradeQueryResult extends AliPayBaseResult {
    /**
     * 商家订单号
     */
    private String tradeNo;
    /**
     * 支付宝交易号
     */
    private String outTradeNo;
    /**
     * 交易状态
     */
    private AliPayTradeStatusEnum tradeStatus;
    /**
     * 交易的订单金额，单位为元，两位小数
     */
    private BigDecimal totalAmount;
    /**
     * 支付时间
     */
    private LocalDateTime paidTime;
    /**
     * 实收金额，单位为元，两位小数。该金额为本笔交易，商户账户能够实际收到的金额
     */
    private BigDecimal receiptAmount;
    /**
     * 买家实付金额，单位为元，两位小数。该金额代表该笔交易买家实际支付的金额，不包含商户折扣等金额
     */
    private BigDecimal buyerPayAmount;
    /**
     * 买家支付宝账号
     */
    private String buyerLogonId;
    /**
     * 买家在支付宝的用户id
     */
    private String buyerUserId;
    /**
     * 买家名称
     */
    private String buyerUserName;
    /**
     * 买家用户类型。CORPORATE:企业用户；PRIVATE:个人用户。
     */
    private String buyerUserType;
    /**
     * 平台优惠金额
     */
    private BigDecimal discountAmount;
    /**
     * 商家优惠金额
     */
    private BigDecimal mdiscountAmount;

    public static AliPayTradeQueryResult ofSuccess(AlipayTradeQueryResponse response) {
        //@formatter:off
        return AliPayTradeQueryResult
                .builder()
                .success(true)
                .message(response.getMsg())
                .code(response.getCode())
                .outTradeNo(response.getTradeNo())
                .tradeNo(response.getOutTradeNo())
                .tradeStatus(AliPayTradeStatusEnum.parse(response.getTradeStatus()))
                .totalAmount(new BigDecimal(response.getTotalAmount()))
                .receiptAmount(StringUtils.isNotEmpty(response.getReceiptAmount())?new BigDecimal(response.getReceiptAmount()):null)
                .paidTime(response.getSendPayDate() != null ? LocalDateTime.ofInstant(response.getSendPayDate().toInstant(), ZoneId.systemDefault()) : null)
                .discountAmount(StringUtils.isNotEmpty(response.getDiscountAmount())?new BigDecimal(response.getDiscountAmount()):null)
                .mdiscountAmount(StringUtils.isNotEmpty(response.getMdiscountAmount())?new BigDecimal(response.getMdiscountAmount()):null)
                .buyerPayAmount(StringUtils.isNotEmpty(response.getBuyerPayAmount())?new BigDecimal(response.getBuyerPayAmount()):null)
                .buyerLogonId(response.getBuyerLogonId())
                .buyerUserId(response.getBuyerUserId())
                .buyerUserName(response.getBuyerUserName())
                .buyerUserType(response.getBuyerUserType())
                .build();
    }

    public static AliPayTradeQueryResult ofFail(AlipayTradeQueryResponse response) {
        return AliPayTradeQueryResult.builder().success(false).message(response.getSubMsg()).code(response.getSubCode())
                .build();
    }

    public static AliPayTradeQueryResult ofError() {
        return AliPayTradeQueryResult.builder().success(false).code("unknow-error").message("服务暂不可用").build();
    }
}
