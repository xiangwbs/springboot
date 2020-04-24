package com.xwbing.domain.entity.model;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;

import lombok.Data;

/**
 * @author daofeng
 * @version $Id$
 * @since 2020年04月15日 上午9:33
 */
@Data
public class EasyExcelModel {
    @ExcelProperty(value = "姓名",index = 0)
    private String name;
    @ExcelProperty(value = "年龄", index = 1)
    private int age;
    @ExcelIgnore
    private String ignore;
}
