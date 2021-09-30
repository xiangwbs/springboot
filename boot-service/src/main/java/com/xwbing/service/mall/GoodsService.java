package com.xwbing.service.mall;

import org.springframework.stereotype.Service;

import com.xwbing.service.domain.entity.rest.Goods;
import com.xwbing.service.domain.mapper.rest.GoodsMapper;
import com.xwbing.service.exception.BusinessException;
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

    public void decreaseStock(String id, Integer num) {
        int r = goodsMapper.decreaseStock(id, num);
        if (r != 1) {
            throw new BusinessException("减库存失败");
        }
    }

    public void increaseStock(String id, Integer num) {
        goodsMapper.increaseStock(id, num);
    }
}
