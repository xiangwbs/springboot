package com.xwbing.domain.entity.vo;

import com.alibaba.excel.annotation.ExcelProperty;

import lombok.Data;

/**
 * @author daofeng
 * @version $Id$
 * @since 2020年04月15日 上午9:33
 */
@Data
public class ExcelVo {
    @ExcelProperty(index = 0)
    private String name;
    @ExcelProperty(index = 1)
    private Integer age;
    @ExcelProperty(index = 2)
    private String tel;
    @ExcelProperty(index = 3)
    private String introduction;
}
