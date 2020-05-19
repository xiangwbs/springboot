package com.xwbing.domain.entity.rest;

import java.util.Date;

import com.xwbing.domain.entity.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author xiangwb
 */
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TradeRecord extends BaseEntity {
    private static final long serialVersionUID = 3203895800240700542L;
    /**
     * 网关返回码
     */
    private String code;
    /**
     * 网关返回码描述
     */
    private String msg;
    /**
     * 业务返回码
     */
    private String subCode;
    /**
     * 业务返回码描述
     */
    private String subMsg;
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
     * 状态 PAYING、SUCCESS、FAIL、CLOSED
     */
    private String status;
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
     * NotifyStatusEnums
     */
    private String notifyStatus;
    /**
     * 支付类型
     */
    private String payType;
}