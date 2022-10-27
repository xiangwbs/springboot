package com.xwbing.web.controller.demo;

import org.springframework.context.annotation.Configuration;

import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.config.annotation.NacosConfigurationProperties;

import lombok.Data;

/**
 * @author daofeng
 * @version $Id$
 * @since 2022年10月27日 4:53 PM
 */
@Data
@NacosConfigurationProperties(prefix = "thread.pool", dataId = "com.xwbing.bot-test.yaml", type = ConfigType.YAML, autoRefreshed = true)
@Configuration
public class ThreadPoolProperties {
    private int coreThread;
    private int maxThread;
}
