package com.xwbing.service.demo.sql;

import java.sql.*;

/**
 * @author daofeng
 * @version $
 * @since 2024年05月29日 3:53 PM
 */
public class DynamicDemo {
    public static void query(String sql) {
        String jdbcUrl = "jdbc:mysql://127.0.0.1:3306/boot";
        String username = "root";
        String password = "xiangwbs";
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnsCount = metaData.getColumnCount();
            while (resultSet.next()) {
                for (int i = 1; i <= columnsCount; i++) {
                    // 获取列的名字
                    String columnName = metaData.getColumnName(i);
                    // 获取对应列的值
                    Object columnValue = resultSet.getObject(i);
                    // 打印列名和值
                    System.out.print(columnName + ": " + columnValue + "\n");
                }
            }
            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        query("SELECT DISTINCT(name) from sys_user_info");
    }
}
