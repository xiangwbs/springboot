package com.xwbing.domain.entity.vo;

import com.alibaba.excel.annotation.ExcelProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author daofeng
 * @version $Id$
 * @since 2020年04月15日 上午9:33
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExcelVo {
    @ExcelProperty(index = 0, value = "姓名")
    private String name;
    @ExcelProperty(index = 1, value = "年龄")
    private Integer age;
    @ExcelProperty(index = 2, value = "电话")
    private String tel;
    @ExcelProperty(index = 3, value = "简介")
    private String introduction;
}
