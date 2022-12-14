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
public class ExcelHeaderDemoVo {
    @ExcelProperty(index = 0, value = "第一列")
    private String c1;
    @ExcelProperty(index = 1, value = "第二列")
    private String c2;
    @ExcelProperty(index = 2, value = "第三列")
    private String c3;
    @ExcelProperty(index = 3, value = "第四列")
    private String c4;
}
