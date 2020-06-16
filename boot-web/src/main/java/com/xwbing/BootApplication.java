package com.xwbing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

import com.xwbing.config.redis.EnableRedis;
import com.xwbing.config.clusterseq.EnableClusterSeq;

/**
 * 项目名称: boot-module-pro
 * 创建时间: 2017/11/2 18:19
 * 作者: boot-module-pro
 * 说明: 启动类
 */
@EnableRedis
@EnableClusterSeq
@ServletComponentScan
@SpringBootApplication
@EnableAspectJAutoProxy
@EnableAsync
public class BootApplication {
    public static void main(String[] args) {
        SpringApplication.run(BootApplication.class, args);
        // SpringApplication springApplication = new SpringApplication(BootApplication.class);
        // springApplication.run(args);
    }
}
