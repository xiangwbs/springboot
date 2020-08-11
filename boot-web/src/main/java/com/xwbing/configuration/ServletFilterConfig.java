package com.xwbing.configuration;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.xwbing.handler.FileFilter;
import com.xwbing.util.captcha.CaptchaServlet;

import lombok.extern.slf4j.Slf4j;

/**
 * 说明: 统一servlet/filter配置
 * 创建时间: 2017/5/10 16:36
 * 作者:  xiangwb
 * 推荐使用注解@WebFilter和@WebServlet
 */
@Slf4j
@Configuration
public class ServletFilterConfig {
    @Bean
    public ServletRegistrationBean captchaServlet() {
        log.info("注册登陆验证码CaptchaServlet ======================= ");
        ServletRegistrationBean registration = new ServletRegistrationBean(new CaptchaServlet());
        registration.addUrlMappings("/captcha");
        return registration;
    }

    /*servlet*****************************************************filter*/
    @Bean
    public FilterRegistrationBean fileFilter() {
        log.info("注册防盗链过滤器FileFilter ======================= ");
        FilterRegistrationBean registration = new FilterRegistrationBean(new FileFilter());
        registration.addUrlPatterns("/file/*");
        return registration;
    }
}

