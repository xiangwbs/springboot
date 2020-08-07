package com.xwbing.config.aliyun.rocketmq;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import com.aliyun.openservices.ons.api.MessageListener;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.bean.ConsumerBean;
import com.aliyun.openservices.ons.api.bean.Subscription;

import lombok.extern.slf4j.Slf4j;

/**
 * @author daofeng
 * @version $
 * @since 2020年08月06日 20:11
 */
@Slf4j
public class OnsConsumer implements BeanPostProcessor {
    private Properties properties;
    private List<ConsumerBean> consumers = new ArrayList<>();

    public OnsConsumer(Properties properties) {
        this.properties = properties;
    }

    public void shutdown() {
        consumers.forEach(ConsumerBean::shutdown);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> clazz = bean.getClass();
        OnsListener listener = clazz.getAnnotation(OnsListener.class);
        if (listener != null) {
            ConsumerBean consumer = new ConsumerBean();
            //配置文件
            properties.put(PropertyKeyConst.GROUP_ID, listener.groupId());
            consumer.setProperties(properties);
            //订阅关系
            Subscription subscription = new Subscription();
            subscription.setTopic(listener.topic());
            subscription.setExpression(listener.expression());
            subscription.setType(listener.type());
            consumer.setSubscriptionTable(Collections.singletonMap(subscription, (MessageListener)bean));
            consumer.start();
            consumers.add(consumer);
        }
        return bean;
    }
}