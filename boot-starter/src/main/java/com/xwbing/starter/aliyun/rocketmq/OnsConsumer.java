package com.xwbing.starter.aliyun.rocketmq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.aliyun.openservices.ons.api.MessageListener;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.bean.ConsumerBean;
import com.aliyun.openservices.ons.api.bean.Subscription;
import com.aliyun.openservices.ons.api.exception.ONSClientException;

import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

/**
 * 所有Consumer实例管理
 *
 * @author daofeng
 * @version $
 * @since 2020年08月06日 20:11
 */
@Slf4j
public class OnsConsumer implements ApplicationContextAware, InitializingBean, DisposableBean {
    private final OnsProperties onsProperties;
    private ApplicationContext applicationContext;
    private final Map<String, ConsumerBean> consumerMap = new HashMap<>();
    private final List<SubscriptionMeta> subscriptionMetas = new ArrayList<>();

    public OnsConsumer(OnsProperties onsProperties) {
        this.onsProperties = onsProperties;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void destroy() {
        consumerMap.values().forEach(ConsumerBean::shutdown);
    }

    @Override
    public void afterPropertiesSet() {
        Map<String, Object> listenerMap = applicationContext.getBeansWithAnnotation(OnsListener.class);
        if (listenerMap.isEmpty()) {
            return;
        }
        // 设置配置文件
        listenerMap.values().forEach(bean -> {
            OnsListener listener = bean.getClass().getAnnotation(OnsListener.class);
            SubscriptionMeta subscriptionMeta = SubscriptionMeta.builder().onsListener(listener)
                    .messageListener((MessageListener)bean).build();
            subscriptionMetas.add(subscriptionMeta);
            ConsumerBean consumer = consumerMap.get(listener.groupId());
            Properties properties;
            // 一个GroupID在jvm里只能创建一个Consumer实例
            if (consumer == null) {
                consumer = new ConsumerBean();
                properties = OnsAutoConfiguration.buildProperties(onsProperties);
                properties.put(PropertyKeyConst.GROUP_ID, listener.groupId());
                consumer.setProperties(properties);
                consumerMap.put(listener.groupId(), consumer);
            }
        });
        // 订阅 1-n topic
        // 一个topic只能对应一个MessageListener 参考Subscription.hashCode()，Subscription.equals()
        Map<String, List<SubscriptionMeta>> metaMap = subscriptionMetas.stream()
                .collect(Collectors.groupingBy(subscriptionMeta -> subscriptionMeta.getOnsListener().groupId()));
        consumerMap.forEach((groupId, consumerBean) -> {
            List<SubscriptionMeta> subscriptionMetas = metaMap.get(groupId);
            List<String> topics = subscriptionMetas.stream()
                    .map(subscriptionMeta -> subscriptionMeta.getOnsListener().topic()).collect(Collectors.toList());
            //校验是否存在重复topic
            checkTopic(groupId, topics);
            Map<Subscription, MessageListener> subscriptionTable = new HashMap<>();
            OnsListener onsListener;
            Subscription subscription;
            for (SubscriptionMeta subscriptionMeta : subscriptionMetas) {
                onsListener = subscriptionMeta.getOnsListener();
                subscription = new Subscription();
                subscription.setTopic(onsListener.topic());
                subscription.setExpression(onsListener.expression());
                subscription.setType(onsListener.type());
                subscriptionTable.put(subscription, subscriptionMeta.getMessageListener());
            }
            consumerBean.setSubscriptionTable(subscriptionTable);
            consumerBean.start();
        });
    }

    @Value
    @Builder
    private static class SubscriptionMeta {
        OnsListener onsListener;
        MessageListener messageListener;
    }

    private void checkTopic(String groupId, List<String> topics) {
        Map<String, Integer> map = new HashMap<>();
        for (String topic : topics) {
            Integer i = 1;
            if (map.get(topic) != null) {
                i = map.get(topic) + 1;
            }
            map.put(topic, i);
        }
        map.keySet().forEach(topic -> {
            if (map.get(topic) > 1) {
                throw new ONSClientException("duplicate topic [" + topic + "] by group id [" + groupId + "]");
            }
        });
    }
}