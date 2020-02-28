package com.xwbing.rabbit;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 创建时间: 2018/4/25 14:28
 * 作者: xiangwb
 * 说明: topic交换机配置
 */
@Configuration
public class TopicExchangeConfig {
    /**
     * 声明队列
     *
     * @return
     */
    @Bean
    public Queue emailQueue() {
        return new Queue(RabbitConstant.EMAIL_QUEUE, true);//true表示持久化该队列
    }

    @Bean
    public Queue httpRequestQueue() {
        return new Queue(RabbitConstant.MESSAGE_QUEUE, true);
    }

    /**
     * 声明交换机
     *
     * @return
     */
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(RabbitConstant.CONTROL_EXCHANGE);
    }

    /**
     * 绑定
     *
     * @return
     */
    @Bean
    public Binding bindingEmail() {
        return BindingBuilder.bind(emailQueue()).to(topicExchange()).with(RabbitConstant.EMAIL_ROUTING_KEY);//*表示一个词,#表示零个或多个词
    }

    @Bean
    public Binding bindingHttpRequest() {
        return BindingBuilder.bind(httpRequestQueue()).to(topicExchange()).with(RabbitConstant.MESSAGE_ROUTING_KEY);
    }
}
