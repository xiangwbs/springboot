package com.xwbing.service.mall.dto;

import com.xwbing.service.mall.enums.PayWayEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 网站支付请求对象
 *
 * @author daofeng
 * @version $
 * @since 2020年07月27日 下午10:01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeReqDTO {
    /**
     * 支付方式
     */
    private PayWayEnum payWay;
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 流水号
     */
    private String tradeNo;
    /**
     * 付款金额,单位为分
     */
    private Long totalAmount;
    /**
     * 订单标题
     */
    private String subject;
    /**
     * 支付授权码
     */
    private String authCode;
    /**
     * 买家的支付宝唯一用户号（2088开头的16位纯数字）
     */
    private String aliPayBuyerId;
    /**
     * 微信公众号/小程序openid
     */
    private String weChatOpenid;
    /**
     * 微信公众号/小程序appId
     */
    private String weChatAppId;

    public static TradeReqDTO of(PayWayEnum payWay, String tradeNo, String subject, Long totalAmount) {
        //@formatter:off
        return TradeReqDTO
                .builder()
                .payWay(payWay)
                .tradeNo(tradeNo)
                .subject(subject)
                .totalAmount(totalAmount)
                .build();
        //@formatter:on
    }

    public static TradeReqDTO of(PayWayEnum payWay, String tradeNo, String subject, Long totalAmount, String authCode) {
        //@formatter:off
        return TradeReqDTO
                .builder()
                .payWay(payWay)
                .tradeNo(tradeNo)
                .subject(subject)
                .totalAmount(totalAmount)
                .authCode(authCode)
                .build();
        //@formatter:on
    }

    public static TradeReqDTO of(PayWayEnum payWay, String aliPayBuyerId, String tradeNo, String subject,
            Long totalAmount) {
        //@formatter:off
        return TradeReqDTO
                .builder()
                .payWay(payWay)
                .tradeNo(tradeNo)
                .subject(subject)
                .totalAmount(totalAmount)
                .aliPayBuyerId(aliPayBuyerId)
                .build();
        //@formatter:on
    }
}
