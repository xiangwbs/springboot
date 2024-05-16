package com.xwbing.service.domain.entity.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author daofeng
 * @version $
 * @since 2024年05月16日 1:35 PM
 */
@Data
public class Nl2sqlExcelDTO {
    @ExcelProperty(index = 0, value = "计算类型")
    private String type;
    @ExcelProperty(index = 1, value = "问句")
    private String question;
    @ExcelProperty(index = 2, value = "sql")
    private String sql;
    @ExcelProperty(index = 3, value = "模型sql")
    private String modelSql;
    @ExcelProperty(index = 4, value = "是否正确")
    private String correct;
}