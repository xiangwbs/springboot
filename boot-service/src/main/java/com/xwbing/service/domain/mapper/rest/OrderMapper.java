package com.xwbing.service.domain.mapper.rest;

import com.xwbing.service.domain.entity.rest.Order;
import com.xwbing.service.domain.mapper.BaseMapper;

/**
 * @author xiangwb
 */
public interface OrderMapper extends BaseMapper<Order> {
    Order getByOrderNo(String orderNo);
}