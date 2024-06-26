package com.xwbing.service.demo.sql;

import cn.hutool.json.JSONUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.sql.*;
import java.util.*;

/**
 * @author daofeng
 * @version $
 * @since 2024年05月29日 3:53 PM
 */
@Slf4j
public class JdbcUtil {
    public static List<Map<String, Object>> querySql(String url, String username, String password, String sql) {
        List<Map<String, Object>> list = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    int columnsCount = metaData.getColumnCount();
                    while (resultSet.next()) {
                        Map<String, Object> rowMap = new LinkedHashMap<>();
                        for (int i = 1; i <= columnsCount; i++) {
                            String column = StringUtils.isNotEmpty(metaData.getColumnLabel(i)) ? metaData.getColumnLabel(i) : metaData.getColumnName(i);
                            rowMap.put(column.toLowerCase(), resultSet.getObject(i));
                        }
                        list.add(rowMap);
                    }
                    return list;
                }
            }
        } catch (SQLException e) {
            log.error("jdbcUtil querySql sql:{} error", sql, e);
            return Collections.emptyList();
        }
    }

    public static List<Column> queryColumn(String url, String username, String password, String tableName) {
        List<Column> list = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String sql = null;
            if (url.contains(":mysql:")) {
                String schema = url.replaceAll("jdbc:mysql://", "");
                schema = schema.substring(schema.indexOf('/') + 1);
                if (schema.contains("?")) {
                    schema = schema.substring(0, schema.indexOf('?'));
                }
                sql = "SELECT COLUMN_NAME name,COLUMN_COMMENT description,DATA_TYPE type FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '" + schema + "' AND TABLE_NAME = '" + tableName + "'";
            } else if (url.contains(":dm:")) {
                sql = "SELECT col.COLUMN_NAME name,col.DATA_TYPE type,com.COMMENTS description FROM user_tab_columns col INNER JOIN user_col_comments com ON col.TABLE_NAME = com.TABLE_NAME AND col.COLUMN_NAME = com.COLUMN_NAME WHERE col.TABLE_NAME = '" + tableName.toUpperCase() + "'";
            }
            if (sql == null) {
                return Collections.emptyList();
            }
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    ResultSetMetaData metaData = resultSet.getMetaData();
                    int columnsCount = metaData.getColumnCount();
                    while (resultSet.next()) {
                        Map<String, Object> rowMap = new LinkedHashMap<>();
                        for (int i = 1; i <= columnsCount; i++) {
                            String column = StringUtils.isNotEmpty(metaData.getColumnLabel(i)) ? metaData.getColumnLabel(i) : metaData.getColumnName(i);
                            rowMap.put(column.toLowerCase(), resultSet.getString(i).toLowerCase());
                        }
                        list.add(JSONUtil.toBean(JSONUtil.toJsonStr(rowMap), Column.class));
                    }
                    return list;
                }
            }
        } catch (SQLException e) {
            log.error("jdbcUtil queryColumn tableName:{} error", tableName, e);
            return Collections.emptyList();
        }
    }

    @Data
    public static class Column {
        private String name;
        private String description;
        private String type;
    }

    public static void main(String[] args) {
        String jdbcUrl = "jdbc:mysql://127.0.0.1:3306/boot";
        String username = "root";
        String password = "xiangwbs";
        List<Map<String, Object>> dataList = querySql(jdbcUrl, username, password, "SELECT * from sys_user_info");
        List<Column> columnList = queryColumn(jdbcUrl, username, password, "sys_user_info");
        System.out.println("");
    }
}
