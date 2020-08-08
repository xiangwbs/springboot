package com.xwbing.config.aliyun.rocketmq;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.commons.collections.MapUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.aliyun.openservices.ons.api.MessageListener;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.bean.ConsumerBean;
import com.aliyun.openservices.ons.api.bean.Subscription;

import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

/**
 * @author daofeng
 * @version $
 * @since 2020年08月06日 20:11
 */
@Slf4j
public class OnsConsumer implements ApplicationContextAware, InitializingBean, DisposableBean {
    private ApplicationContext applicationContext;
    private OnsProperties onsProperties;
    private Map<String, ConsumerBean> consumerMap = new HashMap<>();
    private List<SubscriptionMeta> subscriptionMetas = new ArrayList<>();

    public OnsConsumer(OnsProperties onsProperties) {
        this.onsProperties = onsProperties;
    }

    @Override
    public void destroy() {
        consumerMap.values().forEach(ConsumerBean::shutdown);
    }

    @Override
    public void afterPropertiesSet() {
        Map<String, Object> beanMap = applicationContext.getBeansWithAnnotation(OnsListener.class);
        if (MapUtils.isNotEmpty(beanMap)) {
            beanMap.values().forEach(bean -> {
                Class<?> clazz = bean.getClass();
                OnsListener listener = clazz.getAnnotation(OnsListener.class);
                SubscriptionMeta subscriptionMeta = SubscriptionMeta.builder().onsListener(listener)
                        .messageListener((MessageListener)bean).build();
                subscriptionMetas.add(subscriptionMeta);
                String groupId = listener.groupId();
                ConsumerBean consumer = consumerMap.get(groupId);
                Properties properties;
                // 一个Group ID只能创建一个Consumer实例
                if (consumer == null) {
                    // 设置配置文件
                    consumer = new ConsumerBean();
                    properties = OnsConfiguration.buildProperties(onsProperties);
                    properties.put(PropertyKeyConst.GROUP_ID, listener.groupId());
                    consumer.setProperties(properties);
                    consumerMap.put(groupId, consumer);
                }
            });
            // 订阅 1-n topic
            // 一个topic只能创建一个MessageListener 参考Subscription.hashCode()
            Map<String, List<SubscriptionMeta>> metaMap = subscriptionMetas.stream()
                    .collect(Collectors.groupingBy(subscriptionMeta -> subscriptionMeta.getOnsListener().groupId()));
            consumerMap.forEach((groupId, consumerBean) -> {
                Map<Subscription, MessageListener> subscriptionTable = new HashMap<>();
                OnsListener onsListener;
                Subscription subscription;
                for (SubscriptionMeta subscriptionMeta : metaMap.get(groupId)) {
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
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Value
    @Builder
    private static class SubscriptionMeta {
        OnsListener onsListener;
        MessageListener messageListener;
    }
}