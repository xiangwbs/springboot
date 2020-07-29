package com.xwbing.service.pay.vo;

import com.alipay.api.response.AlipayTradeQueryResponse;
import com.xwbing.service.pay.enums.AliPayTradeStatusEnum;

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
    private String outTradeNo;
    /**
     * 支付宝交易号
     */
    private String tradeNo;
    /**
     * 交易状态
     */
    private AliPayTradeStatusEnum tradeStatus;
    /**
     * 交易的订单金额，单位为元，两位小数
     */
    private String totalAmount;
    /**
     * 实收金额，单位为元，两位小数。该金额为本笔交易，商户账户能够实际收到的金额
     */
    private String receiptAmount;
    /**
     * 平台优惠金额
     */
    private String discountAmount;
    /**
     * 买家实付金额，单位为元，两位小数。该金额代表该笔交易买家实际支付的金额，不包含商户折扣等金额
     */
    private String buyerPayAmount;
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

    public static AliPayTradeQueryResult ofSuccess(AlipayTradeQueryResponse response) {
        return AliPayTradeQueryResult.builder().success(true).
                message(response.getMsg()).code(response.getCode()).outTradeNo(response.getOutTradeNo())
                .tradeNo(response.getTradeNo()).tradeStatus(AliPayTradeStatusEnum.parse(response.getTradeStatus()))
                .totalAmount(response.getTotalAmount()).receiptAmount(response.getReceiptAmount())
                .discountAmount(response.getDiscountAmount()).buyerPayAmount(response.getBuyerPayAmount())
                .buyerLogonId(response.getBuyerLogonId()).buyerUserId(response.getBuyerUserId())
                .buyerUserType(response.getBuyerUserType()).build();
    }

    public static AliPayTradeQueryResult ofFail(AlipayTradeQueryResponse response) {
        return AliPayTradeQueryResult.builder().success(false).message(response.getSubMsg()).code(response.getSubCode())
                .build();
    }

    public static AliPayTradeQueryResult ofError() {
        return AliPayTradeQueryResult.builder().success(false).code("unknow-error").message("服务暂不可用").build();
    }
}
