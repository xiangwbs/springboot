package com.xwbing.starter.aliyun.rocketmq;

import java.util.Properties;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.bean.OrderProducerBean;
import com.aliyun.openservices.ons.api.bean.ProducerBean;

import lombok.extern.slf4j.Slf4j;

/**
 * 阿里云RocketMQ生产者与消费者启动集成
 *
 * RocketMQ的使用参考文档：https://help.aliyun.com/document_detail/29553.html?spm=a2c4g.11186623.6.571.7566115dmdFvBz
 *
 * @author daofeg
 * @version $
 * @since 2020年08月06日 20:54
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = OnsProperties.PREFIX, name = "name-server-address")
@EnableConfigurationProperties(OnsProperties.class)
public class OnsAutoConfiguration {
    private final OnsProperties onsProperties;

    public OnsAutoConfiguration(OnsProperties onsProperties) {
        this.onsProperties = onsProperties;
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    @ConditionalOnProperty(prefix = OnsProperties.PREFIX, name = "producer-group-id")
    public ProducerBean producer(OnsProperties onsProperties) {
        Properties properties = buildProperties(onsProperties);
        properties.put(PropertyKeyConst.GROUP_ID, onsProperties.getProducerGroupId());
        ProducerBean producerBean = new ProducerBean();
        producerBean.setProperties(properties);
        return producerBean;
    }

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    @ConditionalOnProperty(prefix = OnsProperties.PREFIX, name = "producer-group-id")
    public OrderProducerBean orderProducer() {
        Properties properties = buildProperties(onsProperties);
        properties.put(PropertyKeyConst.GROUP_ID, onsProperties.getProducerGroupId());
        OrderProducerBean producerBean = new OrderProducerBean();
        producerBean.setProperties(properties);
        return producerBean;
    }

    @Bean
    @ConditionalOnProperty(prefix = OnsProperties.PREFIX, name = "producer-group-id")
    public OnsTemplate onsTemplate(ProducerBean producerBean, OrderProducerBean orderProducerBean) {
        return new OnsTemplate(producerBean, orderProducerBean);
    }

    @Bean
    @ConditionalOnMissingBean(OnsConsumer.class)
    public OnsConsumer mqConsumer() {
        return new OnsConsumer(onsProperties);
    }

    static Properties buildProperties(OnsProperties onsProperties) {
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.AccessKey, onsProperties.getAccessId());
        properties.put(PropertyKeyConst.SecretKey, onsProperties.getAccessSecret());
        properties.put(PropertyKeyConst.NAMESRV_ADDR, onsProperties.getNameServerAddress());
        return properties;
    }
}