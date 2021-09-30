package com.xwbing.service.mall.dto;

import javax.validation.constraints.Min;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author daofeng
 * @version $Id$
 * @since 2021年09月30日 1:01 PM
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderSaveDTO {
    private String userId;
    private String goodsId;
    @Min(value = 1, message = "商品数量不能少于1")
    private Integer num;
}
