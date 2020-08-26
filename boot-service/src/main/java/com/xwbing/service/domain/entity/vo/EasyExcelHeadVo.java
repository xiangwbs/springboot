package com.xwbing.service.domain.entity.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * @author daofeng
 * @version $Id$
 * @since 2020年04月15日 上午9:33
 */
@ColumnWidth(10)
@HeadRowHeight(20)
@ContentRowHeight(15)
@Data
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class EasyExcelHeadVo {
    @ExcelProperty(value = "姓名", index = 0)
    private String name;
    @ExcelProperty(value = "年龄", index = 1)
    private Integer age;
    @ExcelProperty(value = "电话", index = 2)
    private String tel;
    @ColumnWidth(45)
    @ExcelProperty(value = "简介", index = 3)
    private String introduction;
    @ColumnWidth(30)
    @ExcelProperty(value = "说明", index = 4)
    private String remark;
}
