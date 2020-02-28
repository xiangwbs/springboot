package com.xwbing.configuration;

import com.xwbing.constant.CommonEnum;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import springfox.documentation.builders.*;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * 说明: swagger配置
 * 项目名称: boot-module-pro
 * 创建时间: 2017/5/10 16:36
 * 作者:  xiangwb
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket sysDocket() {
        List<Parameter> pars = addParams();
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("system")
                .apiInfo(sysApiInf())
                .useDefaultResponseMessages(false)
                .globalResponseMessage(RequestMethod.GET, customerResponseMessage())
                .globalResponseMessage(RequestMethod.POST, customerResponseMessage())
                .globalOperationParameters(pars)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.xwbing.controller.sys"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo sysApiInf() {
        return new ApiInfoBuilder()
                .title("RESTful API Document")
                .description("系统接口文档")
                .termsOfServiceUrl("http://localhost:8080/swagger-ui.html")
                .contact(new Contact("项伟兵", "https://github.com/xiangwbs/springboot.git", "xiangwbs@163.com"))
                .version("1.0.0")
                .build();
    }

    @Bean
    public Docket restDocket() {
        List<Parameter> pars = addParams();
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("rest")
                .apiInfo(restApiInf())
                .useDefaultResponseMessages(false)
                .globalResponseMessage(RequestMethod.GET, customerResponseMessage())
                .globalResponseMessage(RequestMethod.POST, customerResponseMessage())
                .globalOperationParameters(pars)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.xwbing.controller.rest"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo restApiInf() {
        return new ApiInfoBuilder()
                .title("RESTful API Document")
                .description("接口文档")
                .termsOfServiceUrl("http://localhost:8080/swagger-ui.html")
                .contact(new Contact("项伟兵", "https://github.com/xiangwbs/springboot.git", "xiangwbs@163.com"))
                .version("1.0.0")
                .build();
    }

    /**
     * 添加消息头参数
     *
     * @return
     */
    private List<Parameter> addParams() {
        ParameterBuilder ticketPar = new ParameterBuilder();
        List<Parameter> pars = new ArrayList<>();
        ticketPar.name("token")
                .description("令牌")
                .modelRef(new ModelRef("string"))
                .parameterType("header")
                .required(false).build();
        pars.add(ticketPar.build());
        return pars;
    }

    /**
     * 自定义返回状态信息描述
     *
     * @return
     */
    private ArrayList<ResponseMessage> customerResponseMessage() {
        return new ArrayList<ResponseMessage>() {{
            add(new ResponseMessageBuilder()
                    .code(CommonEnum.CodeEnum.OK.getValue())
                    .message(CommonEnum.CodeEnum.OK.getName())
                    .build());
            add(new ResponseMessageBuilder()
                    .code(CommonEnum.CodeEnum.UNAUTHORIZED.getValue())
                    .message(CommonEnum.CodeEnum.UNAUTHORIZED.getName())
                    .build());
            add(new ResponseMessageBuilder()
                    .code(CommonEnum.CodeEnum.FORBIDDEN.getValue())
                    .message(CommonEnum.CodeEnum.FORBIDDEN.getName())
                    .build());
            add(new ResponseMessageBuilder()
                    .code(CommonEnum.CodeEnum.NOT_FOUND.getValue())
                    .message(CommonEnum.CodeEnum.NOT_FOUND.getName())
                    .build());
            add(new ResponseMessageBuilder()
                    .code(CommonEnum.CodeEnum.ERROR.getValue())
                    .message(CommonEnum.CodeEnum.ERROR.getName())
                    .build());
            add(new ResponseMessageBuilder()
                    .code(CommonEnum.CodeEnum.SERVICE_UNAVAILABLE.getValue())
                    .message(CommonEnum.CodeEnum.SERVICE_UNAVAILABLE.getName())
                    .build());
            add(new ResponseMessageBuilder()
                    .code(CommonEnum.CodeEnum.GATEWAY_TIME_OUT.getValue())
                    .message(CommonEnum.CodeEnum.GATEWAY_TIME_OUT.getName())
                    .build());
        }};
    }
}
