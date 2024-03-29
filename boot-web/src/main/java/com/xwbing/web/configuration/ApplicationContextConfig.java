package com.xwbing.web.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 说明: 程序上下文配置
 * 项目名称: boot-module-pro
 * 创建时间: 2017/5/10 16:36
 * 作者:  xiangwb
 */
@Configuration//相当于.xml文件中的<beans></beans>
//@Import(xxx.class)//用来导入其他配置类
//@ImportResource("classpath:applicationContext.xml")//用来加载其他xml配置文件
public class ApplicationContextConfig {
    /**
     * 任务线程池
     * 无线程可用的处理策略：
     * AbortPolicy(默认) 抛出RejectedExecutionException
     * CallerRunsPolicy 调用者的线程会执行该任务，如果执行器已关闭，则丢弃
     * DiscardPolicy 不能执行的任务将被丢弃
     * DiscardOldestPolicy 如果执行任务尚未关闭，则位于工作队列头部的任务将被删除，然后重试执行程序（如果再次失败，则重复此过程）
     *
     * @return
     */
    @Bean(name = "taskExecutor")
    public ThreadPoolTaskExecutor getPoolTaskExecutor() {
        ThreadPoolTaskExecutor poolTaskExecutor = new ThreadPoolTaskExecutor();
        poolTaskExecutor.setKeepAliveSeconds(10);
        poolTaskExecutor.setCorePoolSize(5);
        poolTaskExecutor.setQueueCapacity(200);
        poolTaskExecutor.setMaxPoolSize(1000);
        //拒绝任务的处理策略
        RejectedExecutionHandler reject = new ThreadPoolExecutor.CallerRunsPolicy();
        poolTaskExecutor.setRejectedExecutionHandler(reject);
        return poolTaskExecutor;
    }

    @Bean
    public org.springframework.web.filter.CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin(CorsConfiguration.ALL);
        corsConfiguration.setAllowedMethods(Arrays.asList(HttpMethod.GET.name(), HttpMethod.POST.name(), HttpMethod.PUT.name(), HttpMethod.DELETE.name(), HttpMethod.OPTIONS.name()));
        corsConfiguration.addAllowedHeader(CorsConfiguration.ALL);
        corsConfiguration.addExposedHeader("token");
        corsConfiguration.setAllowCredentials(Boolean.TRUE);
        source.registerCorsConfiguration("/**", corsConfiguration);
        return new CorsFilter(source);
    }

    // /**
    //  * encoding编码问题(springBoot默认已经配置好,也可以在application.yml里配置)
    //  *
    //  * @return
    //  */
    // @Bean//相当于XML中的<bean></bean>
    // @ConditionalOnMissingBean(CharacterEncodingFilter.class)
    // public Filter characterEncodingFilter() {
    //     CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
    //     characterEncodingFilter.setEncoding("UTF-8");
    //     characterEncodingFilter.setForceEncoding(true);
    //     return characterEncodingFilter;
    // }

    // /**
    //  * 文件上传解析器
    //  * 需关闭自带multipartResolver,否则commonsMultipartResolver解析会得不到数据
    //  * 可在配置文件里配置
    //  *
    //  * @return
    //  */
    // @Bean(name = "multipartResolver")
    // public CommonsMultipartResolver getCommonsMultipartResolver() {
    //     CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
    //     multipartResolver.setMaxUploadSize(10 * 1024 * 1024);//10M
    //     multipartResolver.setDefaultEncoding("UTF-8");
    //     return multipartResolver;
    // }
}

