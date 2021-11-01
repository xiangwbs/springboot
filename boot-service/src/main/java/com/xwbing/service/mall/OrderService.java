package com.xwbing.service.mall;

import java.util.Date;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xwbing.service.domain.entity.rest.Goods;
import com.xwbing.service.domain.entity.rest.Order;
import com.xwbing.service.domain.mapper.rest.OrderMapper;
import com.xwbing.service.exception.BusinessException;
import com.xwbing.service.mall.dto.OrderSaveDTO;
import com.xwbing.service.mall.enums.OrderCloseTypeEnum;
import com.xwbing.service.mall.enums.OrderStatusEnum;
import com.xwbing.service.mall.vo.OrderSaveVO;
import com.xwbing.service.service.BaseService;
import com.xwbing.starter.clusterseq.ClusterSeqGenerator;

import lombok.RequiredArgsConstructor;

/**
 * @author daofeng
 * @version $Id$
 * @since 2021年09月30日 12:40 AM
 */
@RequiredArgsConstructor
@Service
public class OrderService extends BaseService<OrderMapper, Order> {
    private final ClusterSeqGenerator clusterSeqGenerator;
    private final OrderMapper orderMapper;
    private final GoodsService goodsService;
    private final TradeRecordService tradeRecordService;

    @Override
    protected OrderMapper getMapper() {
        return orderMapper;
    }

    @Transactional(rollbackFor = Exception.class)
    public OrderSaveVO save(OrderSaveDTO dto) {
        Goods goods = goodsService.getById(dto.getGoodsId());
        if (goods == null) {
            throw new BusinessException("商品不存在");
        }
        // 保存订单
        String orderNo = String.valueOf(clusterSeqGenerator.getSeqId("order"));
        Order order = Order.builder().orderNo(orderNo).name(goods.getName())
                .actualAmount(goods.getPrice() * dto.getNum()).goodsId(dto.getGoodsId()).goodsNum(dto.getNum())
                .refundableNum(dto.getNum()).status(OrderStatusEnum.WAIT).build();
        super.save(order);
        // 减库存
        goodsService.decreaseStock(dto.getGoodsId(), dto.getNum());
        if (order.getActualAmount() == 0L) {
            //下单成功处理
            this.finish(orderNo, new Date());
            return OrderSaveVO.builder().orderNo(orderNo).needPay(false).build();
        } else {
            //支付处理
            return OrderSaveVO.builder().needPay(true).orderNo(orderNo).build();
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void finish(String orderNo, Date paidTime) {
        Order order = orderMapper.getByOrderNo(orderNo);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (OrderStatusEnum.SUCCESS.equals(order.getStatus())) {
            return;
        }
        Order update = Order.builder().id(order.getId()).status(OrderStatusEnum.SUCCESS).paidTime(paidTime).build();
        super.update(update);
    }

    @Transactional(rollbackFor = Exception.class)
    public void close(String orderNo, OrderCloseTypeEnum closeType) {
        Order order = orderMapper.getByOrderNo(orderNo);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        if (!OrderStatusEnum.WAIT.equals(order.getStatus())) {
            return;
        }
        // 修改订单状态
        Order update = Order.builder().id(order.getId()).status(OrderStatusEnum.CLOSE).closeType(closeType)
                .refundableNum(0).build();
        super.update(update);
        // 取消流水
        tradeRecordService.updateClose(orderNo);
        // 返库存
        goodsService.increaseStock(order.getGoodsId(), order.getGoodsNum());
    }

    public Order getByOrderNo(String orderNo) {
        return orderMapper.getByOrderNo(orderNo);
    }
}
