package com.xwbing.service.mall;

import org.springframework.stereotype.Service;

import com.xwbing.service.domain.entity.rest.Order;
import com.xwbing.service.mall.dto.PayOrderReqDTO;
import com.xwbing.service.mall.dto.TradeReqDTO;
import com.xwbing.service.mall.dto.TradeRespDTO;
import com.xwbing.service.mall.strategy.PayStrategyFactory;

import lombok.RequiredArgsConstructor;

/**
 * @author daofeng
 * @version $Id$
 * @since 2021年09月29日 9:19 PM
 */
@RequiredArgsConstructor
@Service
public class TradeService {
    private final OrderService orderService;
    private final PayStrategyFactory payFactory;

    public TradeRespDTO createPayOrder(PayOrderReqDTO dto) {
        Order order = orderService.getByOrderNo(dto.getOrderNo());
        // 创建流水
        TradeReqDTO req = TradeReqDTO.builder().payWay(dto.getPayWay()).orderNo(dto.getOrderNo())
                .totalAmount(order.getActualAmount()).subject(order.getName()).build();
        return payFactory.getStrategy(dto.getPayType()).createTrade(req);
    }
}
