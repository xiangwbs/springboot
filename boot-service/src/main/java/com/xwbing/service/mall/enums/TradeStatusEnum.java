package com.xwbing.service.mall.enums;

import com.xwbing.service.enums.base.BaseEnum;
import com.xwbing.service.exception.BusinessException;
import com.xwbing.starter.alipay.enums.AliPayTradeStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author daofeng
 * @version $
 * @since 2020年05月19日 19:33
 */
@Getter
@AllArgsConstructor
public enum TradeStatusEnum implements BaseEnum {
    //@formatter:off
    PAYING(1, "支付中"),
    SUCCESS(2, "支付成功"),
    CLOSED(3, "支付关闭"),
    ;

    private final int code;
    private final String name;

    public static TradeStatusEnum of(AliPayTradeStatusEnum aliPayTradeStatus) {
        switch (aliPayTradeStatus) {
            case WAIT_BUYER_PAY:
                return PAYING;
            case TRADE_SUCCESS:
            case TRADE_FINISHED:
                return SUCCESS;
            case TRADE_CLOSED:
                return CLOSED;
            default:
                throw new BusinessException("支付宝流水状态转换异常");
        }
    }
}
