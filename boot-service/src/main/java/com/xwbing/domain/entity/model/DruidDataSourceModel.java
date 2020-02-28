package com.xwbing.domain.entity.model;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
/**
 * 说明: 德鲁伊数据源参数模型
 * 项目名称: boot-module-demo
 * 创建时间: 2017/5/10 16:36
 * 作者:  xiangwb
 */
@Data
@Component
@PropertySource("classpath:druid.properties")
@ConfigurationProperties(prefix = "jdbc")
public class DruidDataSourceModel {
    private String url;
    private String username;
    private String password;
    private String driver;
    private int initSize;
    private int minIdle;
    private int maxActive;
    private int maxWait;
    private int timeBetweenEvictionRunsMillis;
    private int minEvictableIdleTimeMillis;
    private String validationQuery;
    private boolean testWhileIdle;
    private boolean testOnBorrow;
    private boolean testOnReturn;
    private boolean poolPreparedStatements;
    private int maxPoolPreparedStatementPerConnectionSize;
    private String filters;
    private String connectionProperties;
    private boolean useGlobalDataSourceStat;
}
