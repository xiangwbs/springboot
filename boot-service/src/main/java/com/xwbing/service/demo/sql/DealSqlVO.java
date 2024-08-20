package com.xwbing.service.demo.sql;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author daofeng
 * @version $
 * @since 2024年01月23日 1:57 PM
 */
@Data
public class DealSqlVO {
    // 原始sql
    private String originalSql;
    // 有别名的sql
    private String aliasSql;
    // 无别名的sql
    private String noAliasSql;
    // 展示的sql
    private String displaySql;
    // 查询字段列表
    private List<String> selectFieldList;
    // 计算列字段数据类型map
    private Map<String, Byte> functionDataTypeMap;
}