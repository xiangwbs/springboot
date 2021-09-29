package com.xwbing.service.domain.entity.rest;

import com.xwbing.service.domain.entity.BaseEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @description 商品
 * @author xiangwb
 * @date 2021/09/29 21:33
 */
@SuperBuilder(toBuilder = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Goods extends BaseEntity {
    private static final long serialVersionUID = 5617167171665527129L;
    /**
     * 名称
     */
    private String name;
    /**
     * 价格
     */
    private Long price;
    /**
     * 库存
     */
    private Integer stock;
}