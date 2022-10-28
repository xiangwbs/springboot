package com.xwbing.web.controller.demo;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Executor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;

/**
 * 注意：@NacosValue需配合@NacosPropertySource
 *
 * @author daofeng
 * @version $Id$
 * @since 2022年10月27日 11:14 AM
 */
@NacosPropertySource(dataId = "com.xwbing.bot.yaml", type = ConfigType.YAML, autoRefreshed = true)
@RequestMapping("/nacos/config")
@RestController
public class NacosConfigController {
    @Autowired
    private ThreadPoolProperties threadPoolProperties;
    @NacosValue(value = "${thread.pool.coreThread:1}", autoRefreshed = true)
    private int coreThread;

    @GetMapping("/getConfig")
    public void getConfig() {
        System.out.println(threadPoolProperties.getCoreThread() + ":" + threadPoolProperties.getMaxThread());
        System.out.println(coreThread);
    }

    public static void main(String[] args) throws NacosException, IOException {
        Properties properties = new Properties();
        properties.put("serverAddr", "127.0.0.1:8848");
        // 通过nacosfactory创建一个配置中心的服务
        ConfigService configService = NacosFactory.createConfigService(properties);
        // 从远程服务器获取配置
        String content = configService.getConfig("com.xwbing.bot.yaml", "DEFAULT_GROUP", 3000);
        System.out.println(content);

        // 监听配置的变化
        configService.addListener("com.xwbing.bot.yaml", "DEFAULT_GROUP", new Listener() {
            @Override
            public Executor getExecutor() {
                return null;
            }

            @Override
            public void receiveConfigInfo(String configInfo) {
                System.out.println("收到的变更后的配置： " + configInfo);
            }
        });
        // 保证程序不执行结束
        System.in.read();
    }
}