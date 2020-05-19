package com.xwbing.domain.entity.rest;

import java.math.BigDecimal;
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
public class AliPayBillRecord extends BaseEntity {
    private static final long serialVersionUID = 6234236165746729833L;
    /**
     * 财务流水号
     */
    private String accountLogId;
    /**
     * 业务流水号
     */
    private String alipayOrderNo;
    /**
     * 商户订单号
     */
    private String merchantOrderNo;
    /**
     * 支付时间
     */
    private Date paidDate;
    /**
     * 对方账号
     */
    private String otherAccount;
    /**
     * 收入金额
     */
    private BigDecimal inAmount;
    /**
     * 支出金额
     */
    private BigDecimal outAmount;
    /**
     * 账户余额
     */
    private BigDecimal balance;
    /**
     * 业务类型
     */
    private String type;
    /**
     * 备注
     */
    private String remark;
}