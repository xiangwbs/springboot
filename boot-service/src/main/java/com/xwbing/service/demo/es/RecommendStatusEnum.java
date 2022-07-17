package com.xwbing.service.demo.es;

import com.xwbing.service.enums.base.BaseEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author WangGuoxing
 * @version $$
 * @since 2021年07月19日 5:31 下午
 */
@Getter
@AllArgsConstructor
public enum RecommendStatusEnum implements BaseEnum {
    OFF(0, "未推荐"), ON(1, "推荐");

    private final int code;
    private final String desc;
}
