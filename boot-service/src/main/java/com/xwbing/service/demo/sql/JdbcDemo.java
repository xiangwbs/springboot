package com.xwbing.service.demo.sql;

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
    public static List<Map<String, Object>> query(String sql) {
        String jdbcUrl = "jdbc:mysql://127.0.0.1:3306/boot";
        String username = "root";
        String password = "xiangwbs";
        List<Map<String, Object>> list = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
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
            log.error("jdbcUtil query sql:{} error", sql, e);
            return Collections.emptyList();
        }
    }

    public static void main(String[] args) {
        List<Map<String, Object>> query = query("SELECT * from sys_user_info");
        System.out.println("");
    }
}
