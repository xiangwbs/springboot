package com.xwbing.service.mall;

import org.springframework.stereotype.Service;

import com.xwbing.service.domain.entity.rest.Goods;
import com.xwbing.service.domain.mapper.rest.GoodsMapper;
import com.xwbing.service.service.BaseService;

import lombok.RequiredArgsConstructor;

/**
 * @author daofeng
 * @version $Id$
 * @since 2021年09月29日 9:38 PM
 */
@RequiredArgsConstructor
@Service
public class GoodsService extends BaseService<GoodsMapper, Goods> {
    private final GoodsMapper goodsMapper;

    @Override
    protected GoodsMapper getMapper() {
        return goodsMapper;
    }

    public void save(String name, Long price, Integer stock) {
        Goods build = Goods.builder().name(name).price(price).stock(stock).build();
        super.save(build);
    }

    @Override
    public Goods getById(String id) {
        return super.getById(id);
    }
}
