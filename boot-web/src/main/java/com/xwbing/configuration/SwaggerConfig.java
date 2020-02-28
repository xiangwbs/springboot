package com.xwbing.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 说明: swagger配置
 * 项目名称: boot-module-demo
 * 创建时间: 2017/5/10 16:36
 * 作者:  xiangwb
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket sysDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(sysApiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.xwbing.controller.sys"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo sysApiInfo() {
        return new ApiInfoBuilder()
                .title("RESTful API Document")
                .description("系统接口文档")
                .termsOfServiceUrl("http://localhost:8088/doc")
                .contact(new Contact("项伟兵", "https://github.com/xiangwbs/boot-module-pro.git", "xiangwbs@163.com"))
                .version("1.0.0")
                .build();
    }
}
