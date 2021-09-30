package com.xwbing.service.domain.entity.rest;

import java.util.Date;

import com.xwbing.service.domain.entity.BaseEntity;
import com.xwbing.service.mall.enums.OrderCloseTypeEnum;
import com.xwbing.service.mall.enums.OrderStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @description 订单
 * @author xiangwb
 * @date 2021/09/30 13:35
 */
@SuperBuilder(toBuilder = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order extends BaseEntity {
    private static final long serialVersionUID = 2279976839339484737L;
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
     * 实付金额#单位分#
     */
    private Long actualAmount;
    /**
     * 商品数量
     */
    private Integer goodsNum;
    /**
     * 可退款数量
     */
    private Integer refundableNum;
    /**
     * 名称
     */
    private String name;
    /**
     * 状态：10、待支付20、交易成功、30交易失败
     */
    private OrderStatusEnum status;
    /**
     * 支付时间
     */
    private Date paidTime;
    /**
     * 关闭类型:10=关闭,20=超时,30=退款,99=未知
     */
    private OrderCloseTypeEnum closeType;
}