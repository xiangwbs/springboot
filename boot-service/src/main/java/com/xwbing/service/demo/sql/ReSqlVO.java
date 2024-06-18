package com.xwbing.service.demo.sql;

import lombok.Data;

import java.util.Map;

/**
 * @author daofeng
 * @version $
 * @since 2024年01月23日 1:57 PM
 */
@Data
public class ReSqlVO {
    // 处理过的sql
    private String reSql;
    // 没有别名的sql
    private String noAliasSql;
    private Map<String, Byte> functionDataTypeMap;
}