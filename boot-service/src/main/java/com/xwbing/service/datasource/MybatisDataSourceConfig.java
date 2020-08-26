package com.xwbing.service.datasource;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;

/**
 * 说明: MybatisDataSourceConfig
 * 创建时间: 2017/12/10 16:36
 * 作者:  xiangwb
 */
@Configuration
@MapperScan(basePackages = { "com.xwbing.service.domain.mapper" }, sqlSessionFactoryRef = "mybatisSqlSessionFactory")
@PropertySource("classpath:druid.properties")
public class MybatisDataSourceConfig {
    @Primary
    @ConfigurationProperties("db1")
    @Bean(name = "mybatisDatasource")
    public DataSource dataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    @Bean(name = "mybatisSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource());
        sqlSessionFactoryBean.setTypeAliasesPackage("com.xwbing.service.domain.entity");
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sqlSessionFactoryBean.setMapperLocations(resolver.getResources("classpath:mapper/**/*.xml"));
        //DefaultVFS扫包在多模块时获取jar上存在问题，使用SpringBootVFS(java -jar运行会Could not resolve type alias)
        sqlSessionFactoryBean.setVfs(SpringBootVFS.class);
        return sqlSessionFactoryBean.getObject();
    }

    @Primary
    @Bean(name = "mybatisTransactionManager")
    public DataSourceTransactionManager dataSourceTransactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean(name = "mybatisSqlSessionTemplate")
    @Primary
    public SqlSessionTemplate sqlSessionTemplate(
            @Qualifier("mybatisSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean(name = "mybatisTransactionTemplate")
    @Primary
    public TransactionTemplate transactionTemplate(
            @Qualifier("mybatisTransactionManager") DataSourceTransactionManager dataSourceTransactionManager) {
        TransactionTemplate bean = new TransactionTemplate();
        bean.setTransactionManager(dataSourceTransactionManager);
        return bean;
    }
}
