package com.xwbing.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.Filter;

/**
 * 说明: 程序上下文配置
 * 项目名称: boot-module-demo
 * 创建时间: 2017/5/10 16:36
 * 作者:  xiangwb
 */
@Configuration//相当于.xml文件中的<beans></beans>
//@Import(xxx.class)//用来导入其他配置类
//@ImportResource("classpath:applicationContext.xml")//用来加载其他xml配置文件
public class ApplicationContextConfig {
    /**
     * encoding编码问题(springBoot默认已经配置好,也可以在application.yml里配置)
     *
     * @return
     */
    @Bean//相当于XML中的<bean></bean>
    @ConditionalOnMissingBean(CharacterEncodingFilter.class)
    public Filter characterEncodingFilter() {
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceEncoding(true);
        return characterEncodingFilter;
    }

    /**
     * 文件上传解析器
     *
     * @return
     */
    @Bean
    public CommonsMultipartResolver getCommonsMultipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setMaxUploadSize(104857600);
        multipartResolver.setDefaultEncoding("UTF-8");
        return multipartResolver;
    }

    /**
     * 任务线程池
     *
     * @return
     */
    @Bean(name = "taskExecutor")
    public ThreadPoolTaskExecutor getPoolTaskExecutor() {
        ThreadPoolTaskExecutor poolTaskExecutor = new ThreadPoolTaskExecutor();
        poolTaskExecutor.setCorePoolSize(5);//核心线程数
        poolTaskExecutor.setMaxPoolSize(1000);//最大线程数
        poolTaskExecutor.setKeepAliveSeconds(30000);//空闲线程的存活时间
        poolTaskExecutor.setQueueCapacity(200);//队列最大长度
        return poolTaskExecutor;
    }
}
