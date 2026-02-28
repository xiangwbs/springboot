package com.xwbing.service.demo.ws;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

/**
 * @author daiy
 * @since 2021/6/2 13:55
 */
@Configuration
public class WsRedisSubscriberConfig {

    @Autowired
    private WsSendMessageToClientService wsSendMessageToClientService;
    
    /**
     * 消息监听适配器，注入接受消息方法
     *
     * @param listener
     * @return
     */
    @Bean
    public MessageListenerAdapter messageListenerAdapter(WsRedisMessageListener listener) {
        return new MessageListenerAdapter(listener);
    }

    /**
     * 创建消息监听容器
     *
     * @param redisConnectionFactory
     * @param messageListenerAdapter
     * @return
     */
    @Bean
    public RedisMessageListenerContainer getRedisMessageListenerContainer(
            RedisConnectionFactory redisConnectionFactory, MessageListenerAdapter messageListenerAdapter) {
        RedisMessageListenerContainer redisMessageListenerContainer = new RedisMessageListenerContainer();
        redisMessageListenerContainer.setConnectionFactory(redisConnectionFactory);
        redisMessageListenerContainer.addMessageListener(messageListenerAdapter, new PatternTopic(wsSendMessageToClientService.getChannel()));
        return redisMessageListenerContainer;
    }

}
