package com.xwbing.service.demo.es.article;

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
public enum OperationStatusEnum implements BaseEnum {

    OFF_SHELL(0, "下架"),
    ON_SHELL(1, "上架");

    private final int code;
    private final String desc;
}
