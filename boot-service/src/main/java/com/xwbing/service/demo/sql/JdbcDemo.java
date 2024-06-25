package com.xwbing.service.demo.sql;

import cn.hutool.json.JSONUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.*;

/**
 * @author daofeng
 * @version $
 * @since 2024年05月29日 3:53 PM
 */
@Slf4j
public class JdbcDemo {
    public static List<Map<String, Object>> queryData(String url, String username, String password, String sql) {
        List<Map<String, Object>> list = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnsCount = metaData.getColumnCount();
            while (resultSet.next()) {
                Map<String, Object> column = new LinkedHashMap<>();
                for (int i = 1; i <= columnsCount; i++) {
                    column.put(metaData.getColumnName(i).toLowerCase(), resultSet.getObject(i));
                }
                list.add(column);
            }
            resultSet.close();
            statement.close();
            return list;
        } catch (SQLException e) {
            log.error("jdbcUtil queryData sql:{} error", sql, e);
            return Collections.emptyList();
        }
    }

    public static List<Column> queryColumn(String url, String username, String password, String tableName) {
        List<Column> list = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            Statement statement = connection.createStatement();
            String sql = null;
            if (url.contains(":mysql:")) {
                String schema = url.replaceAll("jdbc:mysql://", "");
                schema = schema.substring(schema.indexOf('/') + 1);
                if (schema.contains("?")) {
                    schema = schema.substring(0, schema.indexOf('?'));
                }
                sql = "SELECT TABLE_NAME tablename,COLUMN_NAME columnname,COLUMN_COMMENT columncomment FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = '" + schema + "' AND TABLE_NAME = '" + tableName + "'";
            } else if (url.contains(":dm:")) {
                sql = "SELECT TABLE_NAME tablename,COLUMN_NAME columnname,COMMENTS columncomment FROM user_col_comments WHERE TABLE_NAME = '" + tableName.toUpperCase() + "'";
            }
            if (sql == null) {
                return Collections.emptyList();
            }
            ResultSet resultSet = statement.executeQuery(sql);
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnsCount = metaData.getColumnCount();
            while (resultSet.next()) {
                Map<String, Object> column = new LinkedHashMap<>();
                for (int i = 1; i <= columnsCount; i++) {
                    column.put(metaData.getColumnLabel(i).toLowerCase(), resultSet.getString(i).toLowerCase());
                }
                list.add(JSONUtil.toBean(JSONUtil.toJsonStr(column), Column.class));
            }
            resultSet.close();
            statement.close();
            return list;
        } catch (SQLException e) {
            log.error("jdbcUtil queryColumn tableName:{} error", tableName, e);
            return Collections.emptyList();
        }
    }

    @Data
    public static class Column {
        private String tablename;
        private String columnname;
        private String columncomment;
    }

    public static void main(String[] args) {
        String jdbcUrl = "jdbc:mysql://127.0.0.1:3306/boot";
        String username = "root";
        String password = "xiangwbs";
//        List<Map<String, Object>> dataList = queryData(jdbcUrl, username, password, "SELECT * from sys_user_info");
        List<Column> columnList = queryColumn(jdbcUrl, username, password, "sys_user_info");
        System.out.println("");
    }
}
