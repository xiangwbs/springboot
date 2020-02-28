package com.xwbing.datasource;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.Map;

/**
 * 说明: JpaDataSourceConfig
 * 创建时间: 2017/12/10 16:36
 * 作者:  xiangwb
 */
@Configuration
@PropertySource("classpath:druid.properties")
@EnableJpaRepositories(
        entityManagerFactoryRef = "jpaEntityManagerFactory",
        transactionManagerRef = "jpaTransactionManager",
        basePackages = {"com.xwbing.domain.repository"})
public class JpaDataSourceConfig {
    @Resource
    private JpaProperties jpaProperties;

    @Bean(name = "jpaDataSource")
    @ConfigurationProperties("db2")
    public DataSource dataSource() {
        return DruidDataSourceBuilder.create().build();
    }

    /**
     * 配置entityManagerFactory
     *
     * @param builder
     * @return
     */
    @Bean(name = "jpaEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(dataSource())
                .properties(getVendorProperties(dataSource()))
                .packages("com.xwbing.domain.entity")
                .persistenceUnit("jpaPersistenceUnit")
                .build();
    }

    /**
     * 配置entityManager
     *
     * @param builder
     * @return
     */
    @Bean(name = "japEntityManager")
    public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
        return entityManagerFactory(builder).getObject().createEntityManager();
    }

    /**
     * 配置事务
     *
     * @param builder
     * @return
     */
    @Bean(name = "jpaTransactionManager")
    public PlatformTransactionManager dataSourceTransactionManager(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(entityManagerFactory(builder).getObject());
    }

    /**
     * 获取jpa配置信息
     *
     * @param dataSource
     * @return
     */
    private Map<String, String> getVendorProperties(DataSource dataSource) {
        return jpaProperties.getHibernateProperties(dataSource);
    }
}