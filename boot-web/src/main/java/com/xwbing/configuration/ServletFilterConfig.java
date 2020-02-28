package com.xwbing.configuration;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.xwbing.util.captcha.CaptchaServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 说明: servlet/filter配置
 * 项目名称: boot-module-demo
 * 创建时间: 2017/5/10 16:36
 * 作者:  xiangwb
 */
@Configuration
public class ServletFilterConfig {
    private final Logger logger = LoggerFactory.getLogger(ServletFilterConfig.class);

    @Bean
    public ServletRegistrationBean captchaServlet() {
        logger.info("注册登陆验证码servlet ======================= ");
        ServletRegistrationBean registration = new ServletRegistrationBean(new CaptchaServlet());
        registration.addUrlMappings("/captcha");
        return registration;
    }

    @Bean
    public ServletRegistrationBean statViewServlet() {
        logger.info("注册druid监控信息显示statViewServlet ======================= ");
        ServletRegistrationBean registration = new ServletRegistrationBean(new StatViewServlet());
        registration.addUrlMappings("/druid/*");
        //添加初始化参数:initParams
//        registration.addInitParameter("allow", "127.0.0.1");//IP白名单(没有配置或者为空,则允许所有访问)
//        registration.addInitParameter("deny", "172.17.21.232");//IP黑名单(存在共同时,deny优先于allow)
        registration.addInitParameter("loginUsername", "admin");//用户名
        registration.addInitParameter("loginPassword", "123456");//密码
        registration.addInitParameter("resetEnable", "true");//禁用HTML页面上的"Reset All"功能
        return registration;
    }

    /*servlet*****************************************************filter*/

    @Bean
    public FilterRegistrationBean webStatFilter() {
        logger.info("注册druid监控信息采集webStatFilter ======================= ");
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new WebStatFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,*.jsp,/druid/*");
        filterRegistrationBean.addInitParameter("profileEnable", "true");//监控单个url调用的sql列表
        return filterRegistrationBean;
    }
}
