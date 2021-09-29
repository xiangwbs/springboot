package com.xwbing.service.domain.entity.rest;

import com.xwbing.service.domain.entity.BaseEntity;
import com.xwbing.service.mall.enums.OrderStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author xiangwb
 */
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Order extends BaseEntity {
    private static final long serialVersionUID = 5097065843900896330L;
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 用户id
     */
    private String userId;
    /**
     * 商品id
     */
    private String goodsId;
    /**
     * 总金额#单位分#
     */
    private Long totalAmount;
    /**
     * 商品数量
     */
    private Integer goodsNum;
    /**
     * 可退款数量
     */
    private Integer refundableNum;
    /**
     * 状态：10、待支付20、交易成功、30交易失败
     */
    private OrderStatusEnum status;
}