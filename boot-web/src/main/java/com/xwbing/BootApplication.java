package com.xwbing;

import com.xwbing.config.annotation.EnableRedis;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * 项目名称: boot-module-pro
 * 创建时间: 2017/11/2 18:19
 * 作者: boot-module-pro
 * 说明: 启动类
 */
@EnableRedis
@ServletComponentScan
@SpringBootApplication
public class BootApplication {
    public static void main(String[] args) {
        SpringApplication.run(BootApplication.class, args);
    }
}
