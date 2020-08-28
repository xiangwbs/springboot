package com.xwbing.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

import com.xwbing.starter.alipay.EnableAliPayClient;
import com.xwbing.starter.aliyun.rocketmq.EnableRocketMQ;
import com.xwbing.starter.clusterseq.EnableClusterSeq;
import com.xwbing.starter.clustertask.EnableClusterTask;
import com.xwbing.starter.redis.EnableRedis;

/**
 * 项目名称: boot-module-pro
 * 创建时间: 2017/11/2 18:19
 * 作者: boot-module-pro
 * 说明: 启动类
 */
@EnableRedis
@EnableClusterSeq
@EnableAspectJAutoProxy
@EnableAliPayClient
@EnableRocketMQ
@EnableClusterTask
@ServletComponentScan
@EnableAsync
@SpringBootApplication(scanBasePackages = { "com.xwbing.web","com.xwbing.service" })
public class BootApplication {
    public static void main(String[] args) {
        SpringApplication.run(BootApplication.class, args);
        // SpringApplication springApplication = new SpringApplication(BootApplication.class);
        // springApplication.run(args);
    }
}
