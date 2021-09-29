package com.xwbing.service.pay.enums;

import com.xwbing.service.enums.base.BaseEnum;
import com.xwbing.service.pay.strategy.AliPayStrategy;
import com.xwbing.service.pay.strategy.WeChatPayStrategy;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author daofeng
 * @version $
 * @since 2020年05月19日 19:33
 */
@Getter
@AllArgsConstructor
public enum PayTypeEnum implements BaseEnum {
    //@formatter:off
    WECHAT_PAY(10, "微信支付", WeChatPayStrategy.class.getSimpleName()),
    ALIPAY(20, "支付宝支付", AliPayStrategy.class.getSimpleName()),
    ;

    private final int code;
    private final String desc;
    private final String handleName;
}
