package com.xwbing.service.domain.entity.rest;

import java.util.Date;

import com.xwbing.service.domain.entity.BaseEntity;
import com.xwbing.service.mall.enums.PayTypeEnum;
import com.xwbing.service.mall.enums.TradeStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @description 交易流水表
 * @author xiangwb
 * @date 2021/07/07 14:46
 */
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TradeRecord extends BaseEntity {
    private static final long serialVersionUID = 6959032886650979987L;
    /**
     * 返回码
     */
    private String code;
    /**
     * 返回码描述
     */
    private String msg;
    /**
     * 外部交易号
     */
    private String outTradeNo;
    /**
     * 交易号
     */
    private String tradeNo;
    /**
     * 金额
     */
    private Long amount;
    /**
     * 状态:1=支付中,2=支付成功,3=支付失败,4=已退款
     */
    private TradeStatusEnum status;
    /**
     * 交易内容
     */
    private String subject;
    /**
     * 支付成功时间
     */
    private Date paidDate;
    /**
     * 通知信息
     */
    private String notifyMsg;
    /**
     * notNotified、notified
     */
    private String notifyStatus;
    /**
     * 支付类型
     */
    private PayTypeEnum payType;
}