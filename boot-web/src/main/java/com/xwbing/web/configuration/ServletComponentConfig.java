package com.xwbing.web.configuration;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.xwbing.web.handler.FileFilter;
import com.xwbing.service.util.captcha.CaptchaServlet;

import lombok.extern.slf4j.Slf4j;

/**
 * 说明: 统一servlet/filter配置
 * 创建时间: 2017/5/10 16:36
 * 推荐使用注解@WebFilter和@WebServlet
 *
 * @author xwbing
 */
@Slf4j
@Configuration
public class ServletComponentConfig {
    @Bean
    public ServletRegistrationBean captchaServlet() {
        log.info("注册登陆验证码CaptchaServlet ======================= ");
        ServletRegistrationBean<CaptchaServlet> registration = new ServletRegistrationBean<>();
        registration.setServlet(new CaptchaServlet());
        registration.addUrlMappings("/captcha");
        return registration;
    }

    // ---------------------- servlet ----------------------

    @Bean
    public FilterRegistrationBean fileFilter() {
        log.info("注册防盗链过滤器FileFilter ======================= ");
        FilterRegistrationBean<FileFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new FileFilter());
        registration.addUrlPatterns("/file/*");
        return registration;
    }
}