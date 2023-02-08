package com.xwbing.starter.log.configuration;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.xwbing.starter.log.aspect.OperateLogAspect;
import com.xwbing.starter.log.function.CustomFunctionFactory;
import com.xwbing.starter.log.function.ExampleCustomerFunction;
import com.xwbing.starter.log.function.ICustomFunction;

/**
 * @author daofeng
 * @version $Id$
 * @since 2023年02月07日 4:12 PM
 */
@Configuration
public class OperateLogAutoConfiguration {
    @Bean
    public ICustomFunction exampleFunction() {
        return new ExampleCustomerFunction();
    }

    @Bean
    public CustomFunctionFactory customFunctionFactory(@Autowired List<ICustomFunction> iCustomFunctionList) {
        return new CustomFunctionFactory(iCustomFunctionList);
    }

    @Bean
    public OperateLogAspect operateLogAspect(CustomFunctionFactory customFunctionFactory) {
        return new OperateLogAspect(customFunctionFactory);
    }
}