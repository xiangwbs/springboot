package com.xwbing.service.domain.entity.vo;

import com.alibaba.excel.annotation.ExcelProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author daofeng
 * @version $Id$
 * @since 2020年04月15日 上午9:33
 */
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ExcelHeaderVo {
    @ExcelProperty(index = 0, value = "姓名")
    private String name;
    @ExcelProperty(index = 1, value = "年龄")
    private Integer age;
    @ExcelProperty(index = 2, value = "电话")
    private String tel;
    @ExcelProperty(index = 3, value = "简介")
    private String introduction;
}
