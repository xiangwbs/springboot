package com.xwbing.configuration;

import com.alibaba.druid.pool.DruidDataSource;
import com.xwbing.domain.entity.model.DruidDataSourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.annotation.Resource;
import java.sql.SQLException;

/**
 * 说明: 德鲁伊数据源配置。监控地址:druid/index.html
 * 项目名称: boot-module-demo
 * 创建时间: 2017/12/10 16:36
 * 作者:  xiangwb
 */
@Configuration
//@ConfigurationProperties(prefix = "spring.datasource")
@EnableConfigurationProperties(DruidDataSourceModel.class)
public class DruidDataSourceConfig {
    @Resource
    private DruidDataSourceModel druidDataSourceModel;
    private final Logger logger= LoggerFactory.getLogger(DruidDataSourceConfig.class);

    @Bean
    @Primary
    public DruidDataSource dataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(druidDataSourceModel.getUrl());
        dataSource.setDriverClassName(druidDataSourceModel.getDriver());
        dataSource.setUsername(druidDataSourceModel.getUsername());
        dataSource.setPassword(druidDataSourceModel.getPassword());
        dataSource.setInitialSize(druidDataSourceModel.getInitSize());
        dataSource.setMinIdle(druidDataSourceModel.getMinIdle());
        dataSource.setMaxActive(druidDataSourceModel.getMaxActive());
        dataSource.setMaxWait(druidDataSourceModel.getMaxWait());
        dataSource.setTimeBetweenEvictionRunsMillis(druidDataSourceModel.getTimeBetweenEvictionRunsMillis());
        dataSource.setMinEvictableIdleTimeMillis(druidDataSourceModel.getMinEvictableIdleTimeMillis());
        dataSource.setValidationQuery(druidDataSourceModel.getValidationQuery());
        dataSource.setTestWhileIdle(druidDataSourceModel.isTestWhileIdle());
        dataSource.setTestOnBorrow(druidDataSourceModel.isTestOnBorrow());
        dataSource.setTestOnReturn(druidDataSourceModel.isTestOnReturn());
        dataSource.setPoolPreparedStatements(druidDataSourceModel.isPoolPreparedStatements());
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(druidDataSourceModel.getMaxPoolPreparedStatementPerConnectionSize());
        dataSource.setConnectionProperties(druidDataSourceModel.getConnectionProperties());
        dataSource.setUseGlobalDataSourceStat(druidDataSourceModel.isUseGlobalDataSourceStat());
        try {
            dataSource.setFilters(druidDataSourceModel.getFilters());
        } catch (SQLException e) {
            logger.error("获取druid数据源异常");
        }
        return dataSource;
    }
}
