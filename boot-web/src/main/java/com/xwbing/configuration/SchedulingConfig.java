package com.xwbing.configuration;

import com.xwbing.config.annotation.Lock;
import com.xwbing.util.CommonDataUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 说明: 定时任务定时类
 * 项目名称: boot-module-pro
 * 创建时间: 2017/5/10 16:36
 * 作者:  xiangwb
 */
@Slf4j
@Configuration
@EnableScheduling//启用定时任务
public class SchedulingConfig {
    @Lock
    @Scheduled(cron = "0 0 6 * * ?")//每天6点执行定时任务
    public void scheduler() {
        log.info("清除公共数据类过期数据===================");
        CommonDataUtil.clearExpiryData();
    }
}
