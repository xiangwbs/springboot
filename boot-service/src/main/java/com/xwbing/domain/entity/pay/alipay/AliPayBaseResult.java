package com.xwbing.domain.entity.pay.alipay;

import lombok.Data;

/**
 * 说明: 支付宝接口基础类
 * 项目名称: boot-module-pro
 * 创建时间: 2017/5/10 17:35
 * 作者:  xiangwb
 */
@Data
public class AliPayBaseResult {
    /**
     * 是否成功
     */
    private boolean success;
    /**
     * 错误信息
     */
    private String message;
    /**
     * 错误码
     */
    private String code;
}
