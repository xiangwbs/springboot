package com.xwbing.service.service.pay.vo;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 电脑网站支付参数
 *
 * @author xwbing
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class AliPayTransferParam {
    /**
     * 支持邮箱和手机号格式
     */
    private String payAccount;
    /**
     * 参与方真实姓名
     */
    private String name;
    /**
     * 商户订单号
     */
    private String outBizNo;
    /**
     * 订单总金额，单位为元，精确到小数点后两位 [0.1,100000000]
     */
    private BigDecimal amount;
    /**
     * 转账业务的标题
     */
    private String title;
}
