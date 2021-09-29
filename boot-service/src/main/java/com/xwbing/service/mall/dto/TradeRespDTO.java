package com.xwbing.service.mall.dto;

import com.xwbing.service.mall.enums.TradeStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 支付订单结果信息
 *
 * @author daofeng
 * @version $
 * @since 2020/10/26 14:47
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class TradeRespDTO {
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 内部流水号
     */
    private String tradeNo;
    /**
     * 交易状态
     */
    private TradeStatusEnum tradeStatus;
    /**
     * 描述信息
     */
    private String desc;
    /**
     * 返回数据
     */
    private Object resp;
}
