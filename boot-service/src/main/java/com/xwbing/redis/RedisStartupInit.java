package com.xwbing.redis;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;

/**
 * 说明: 启动时刷新redis缓存
 * 项目名称: boot-module-demo
 * 创建时间: 2017/5/5 16:44
 * 作者:  xiangwb
 */
@Component
@PropertySource("classpath:redis.properties")//只能读取properties文件
public class RedisStartupInit implements InitializingBean {
    private final Logger logger = LoggerFactory.getLogger(RedisStartupInit.class);
    @Resource
    private RedisService redisService;
    @Value("${redisCode}")
    private String redisCode;

    /**
     * 初始化bean的时候会执行该方法
     */
    @Override
    public void afterPropertiesSet() {
        logger.info("redis启动===执行方法===刷新缓存==========================");
        String ping = redisService.ping();
        if (StringUtils.isNotEmpty(ping)) {
            redisService.set("test", "成功");
            logger.info("redis获取测试数据:{}", redisService.get("test"));
            redisService.del("xwb");
            boolean exists = redisService.exists("xwb");
            logger.info("redis删除测试数据:{}", !exists);
            Set<String> set = redisService.keys(redisCode + "*");
            set.forEach(keyStr -> {
                logger.info("删除缓存名称:{}", keyStr);
                redisService.delInit(keyStr);
            });
            logger.info("redis初始化成功=================================");
        }
    }
}
