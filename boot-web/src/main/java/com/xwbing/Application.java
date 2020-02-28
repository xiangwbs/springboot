package com.xwbing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 项目名称: boot-module-demo
 * 创建时间: 2017/11/2 18:19
 * 作者: xiangwb
 * 说明: 启动类
 */
@SpringBootApplication
//@Configuration 定义为配置类,相当于xml文件中的<beans></beans>
//@ComponentScan 组件扫描
//@EnableAutoConfiguration 借助@Import的帮助,将所有符合自动配置条件的bean定义加载到IoC容器
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
