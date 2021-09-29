package com.xwbing.service.mall;

import org.springframework.stereotype.Service;

import com.xwbing.service.domain.entity.rest.Order;
import com.xwbing.service.domain.mapper.rest.OrderMapper;
import com.xwbing.service.service.BaseService;

import lombok.RequiredArgsConstructor;

/**
 * @author daofeng
 * @version $Id$
 * @since 2021年09月30日 12:40 AM
 */
@RequiredArgsConstructor
@Service
public class OrderService extends BaseService<OrderMapper, Order> {
    private final OrderMapper orderMapper;

    @Override
    protected OrderMapper getMapper() {
        return orderMapper;
    }
}
