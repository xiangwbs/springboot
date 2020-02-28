package com.xwbing.domain.entity.pay.wxpay;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * 说明: 微信支付结果基础类
 * 项目名称: boot-module-pro
 * 创建时间: 2017/5/10 17:42
 * 作者:  xiangwb
 */
@Data
public class WxBaseResult {
    /**
     * 返回状态码
     */
    private String resultCode;
    /**
     * 返回信息
     */
    private String message;
    /**
     * 是否成功
     */
    private boolean isSuccess;

    public boolean isSuccess() {
        if (StringUtils.isNotEmpty(resultCode)) {
            return ("SUCCESS".equals(resultCode));
        }
        return isSuccess;
    }

    @Override
    public String toString() {
        return "BaseResult{" + "resultCode='" + resultCode + '\'' + ", message='" + message + '\'' + ", isSuccess=" + isSuccess + '}';
    }
}

