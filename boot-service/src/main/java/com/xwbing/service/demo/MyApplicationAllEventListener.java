package com.xwbing.service.demo;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

/**
 * @author daofeng
 * @version $
 * @since 2024年09月26日 10:24 AM
 */
public class MyApplicationAllEventListener implements ApplicationListener<ApplicationEvent> {
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        // ApplicationStartingEvent 开始启动中
        // prepareEnvironment.ApplicationEnvironmentPreparedEvent 环境已准备好
        // prepareContext.ApplicationContextInitializedEvent 上下文已实例化
        // prepareContext.ApplicationPreparedEvent 上下文已准别好
        // refreshContext.ContextRefreshedEvent
        // ServletWebServerInitializedEvent  Web服务器已初始化(tomcat启动后)
        // ApplicationStartedEvent 应用成功启动
        // ApplicationReadyEvent 应用已准备好
        System.out.println("MyApplicationAllEventListener event:" + event.toString());
    }
}