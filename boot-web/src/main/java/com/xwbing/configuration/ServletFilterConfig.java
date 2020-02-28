package com.xwbing.configuration;

/**
 * 说明: 统一servlet/filter配置
 * 创建时间: 2017/5/10 16:36
 * 作者:  xiangwb
 * 推荐使用注解@WebFilter和@WebServlet
 */
//@Slf4j
//@Configuration
@Deprecated
public class ServletFilterConfig {
//    @Bean
//    public ServletRegistrationBean captchaServlet() {
//        log.info("注册登陆验证码CaptchaServlet ======================= ");
//        ServletRegistrationBean registration = new ServletRegistrationBean(new CaptchaServlet());
//        registration.addUrlMappings("/captcha");
//        return registration;
//    }

    /*servlet*****************************************************filter*/
//    @Bean
//    public FilterRegistrationBean formSaveFilter() {
//        log.info("注册表单重复提交过滤器FormRepeatFilter ======================= ");
//        FilterRegistrationBean registration = new FilterRegistrationBean(new FormRepeatFilter());
//        registration.setEnabled(formSaveFilterEnable);
//        registration.addUrlPatterns("/*");
//        registration.addInitParameter("excludePath", "/doc,/captcha,/v2/api-docs,/swagger-resources,/configuration/ui,/configuration/security,/druid");
//        registration.addInitParameter("excludeType", ".js,.css,.gif,.jpg,.png,.ico,.jsp,.html,/druid/");
//        return registration;
//    }
}

