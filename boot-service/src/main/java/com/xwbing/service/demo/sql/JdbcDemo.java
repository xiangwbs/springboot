package com.xwbing.service.demo.sql;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author daofeng
 * @version $
 * @since 2024年05月29日 3:53 PM
 */
@Slf4j
public class JdbcDemo {
    public static List<JSONObject> query(String sql) {
        String jdbcUrl = "jdbc:mysql://127.0.0.1:3306/boot";
        String username = "root";
        String password = "xiangwbs";
        List<JSONObject> list = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnsCount = metaData.getColumnCount();
            while (resultSet.next()) {
                JSONObject column = new JSONObject();
                for (int i = 1; i <= columnsCount; i++) {
                    column.put(metaData.getColumnName(i), resultSet.getObject(i));
                }
                list.add(column);
            }
            resultSet.close();
            statement.close();
            list = list.stream().map(obj -> {
                JSONObject j = new JSONObject();
                obj.forEach((k, v) -> j.put(k.toLowerCase(), v));
                return j;
            }).collect(Collectors.toList());
            return list;
        } catch (SQLException e) {
            log.error("jdbcUtil query sql:{} error", sql, e);
            return Collections.emptyList();
        }
    }

    public static void main(String[] args) {
        SQLUtils.FormatOption formatOption = new SQLUtils.FormatOption(false,true);
        String sql = SQLUtils.formatMySql("SELECT * from sys_user_info", formatOption);
        List<JSONObject> query = query(sql);
        System.out.println("");

    }
}
