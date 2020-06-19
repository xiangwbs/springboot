package com.xwbing.configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;

import com.xwbing.enums.HttpCodeEnum;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 说明: swagger配置
 * 项目名称: boot-module-pro
 * 创建时间: 2017/5/10 16:36
 * 作者:  xiangwb
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Value("${swagger.enable:false}")
    private Boolean enable = false;

    @Bean
    public Docket sysDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(enable)
                .groupName("system")
                .apiInfo(sysApiInf())
                .useDefaultResponseMessages(false)
                .globalResponseMessage(RequestMethod.GET, customerResponseMessage())
                .globalResponseMessage(RequestMethod.POST, customerResponseMessage())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.xwbing.controller.sys"))
                .paths(PathSelectors.any())
                .build().securitySchemes(securitySchemes()).securityContexts(securityContexts());
    }

    private ApiInfo sysApiInf() {
        return new ApiInfoBuilder()
                .title("RESTful API Document")
                .description("系统接口文档")
                .termsOfServiceUrl("http://localhost:8080/")
                .contact(new Contact("项伟兵", "https://github.com/xiangwbs/springboot.git", "xiangwbs@163.com"))
                .version("1.0.0")
                .build();
    }

    private List<ApiKey> securitySchemes() {
        List<ApiKey> apiKeyList= new ArrayList<>();
        apiKeyList.add(new ApiKey("令牌", "token", "header"));
        apiKeyList.add(new ApiKey("签名", "sign", "header"));
        return apiKeyList;
    }

    private List<SecurityContext> securityContexts() {
        return Collections.singletonList(SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.regex("^(?!auth).*$"))
                .build());
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        List<SecurityReference> securityReferences=new ArrayList<>();
        securityReferences.add(new SecurityReference("令牌", authorizationScopes));
        securityReferences.add(new SecurityReference("签名", authorizationScopes));
        return securityReferences;
    }


    @Bean
    public Docket restDocket() {
        // List<Parameter> pars = addParams();
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(enable)
                .groupName("rest")
                .apiInfo(restApiInf())
                .useDefaultResponseMessages(false)
                .globalResponseMessage(RequestMethod.GET, customerResponseMessage())
                .globalResponseMessage(RequestMethod.POST, customerResponseMessage())
                // .globalOperationParameters(pars)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.xwbing.controller.rest"))
                .paths(PathSelectors.any())
                .build().securitySchemes(securitySchemes()).securityContexts(securityContexts());
    }

    private ApiInfo restApiInf() {
        return new ApiInfoBuilder()
                .title("RESTful API Document")
                .description("接口文档")
                .termsOfServiceUrl("host:port/swagger-ui.html")
                .contact(new Contact("项伟兵", "https://github.com/xiangwbs/springboot.git", "xiangwbs@163.com"))
                .version("1.0.0")
                .build();
    }

    // /**
    //  * 添加消息头参数
    //  *
    //  * @return
    //  */
    // private List<Parameter> addParams() {
    //     ParameterBuilder ticketPar = new ParameterBuilder();
    //     ticketPar.name("token")
    //             .description("令牌")
    //             .modelRef(new ModelRef("string"))
    //             .parameterType("header")
    //             .required(false).build();
    //     return Collections.singletonList(ticketPar.build());
    // }


    /**
     * 自定义返回状态信息描述
     *
     * @return
     */
    private ArrayList<ResponseMessage> customerResponseMessage() {
        return new ArrayList<ResponseMessage>() {
            private static final long serialVersionUID = 2099140398938704631L;

            {
                add(new ResponseMessageBuilder()
                        .code(HttpCodeEnum.OK.getValue())
                        .message(HttpCodeEnum.OK.getName())
                        .build());
                add(new ResponseMessageBuilder()
                        .code(HttpCodeEnum.UNAUTHORIZED.getValue())
                        .message(HttpCodeEnum.UNAUTHORIZED.getName())
                        .build());
                add(new ResponseMessageBuilder()
                        .code(HttpCodeEnum.FORBIDDEN.getValue())
                        .message(HttpCodeEnum.FORBIDDEN.getName())
                        .build());
                add(new ResponseMessageBuilder()
                        .code(HttpCodeEnum.NOT_FOUND.getValue())
                        .message(HttpCodeEnum.NOT_FOUND.getName())
                        .build());
                add(new ResponseMessageBuilder()
                        .code(HttpCodeEnum.ERROR.getValue())
                        .message(HttpCodeEnum.ERROR.getName())
                        .build());
                add(new ResponseMessageBuilder()
                        .code(HttpCodeEnum.SERVICE_UNAVAILABLE.getValue())
                        .message(HttpCodeEnum.SERVICE_UNAVAILABLE.getName())
                        .build());
                add(new ResponseMessageBuilder()
                        .code(HttpCodeEnum.GATEWAY_TIME_OUT.getValue())
                        .message(HttpCodeEnum.GATEWAY_TIME_OUT.getName())
                        .build());
            }
        };
    }
}
