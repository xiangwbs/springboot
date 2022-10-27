package com.xwbing.web.controller.demo;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.NamingEvent;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.xwbing.service.util.Jackson;

/**
 * @author daofeng
 * @version $Id$
 * @since 2022年10月27日 11:14 AM
 */
@RequestMapping("/nacos")
@RestController
public class NacosController {
    @NacosInjected
    private NamingService namingService;

    /**
     * 注册
     */
    @GetMapping("/registry")
    public void registry() throws NacosException {
        Instance instance = new Instance();
        // 集群名称
        instance.setClusterName("boot");
        instance.setEnabled(true);
        // 临时节点/持久节点， 临时节点是AP模式（采用distro算法），持久节点是CP（采用raft算法）
        instance.setEphemeral(true);
        // 健康状态
        instance.setHealthy(true);
        instance.setIp("127.0.0.1");
        instance.setPort(8080);
        // 权重取值范围1到100，数值越大，权重越大
        // instance.setWeight(4.0);
        namingService.registerInstance("boot-module-pro", "myGroup",instance);
    }

    /**
     * 查找服务
     */
    @GetMapping("/discovery")
    public List<Instance> get(@RequestParam String serviceName) throws NacosException {
        return namingService.getAllInstances(serviceName);
    }

    /**
     * 订阅服务
     *
     * 当服务提供者出现服务扩容和缩容时，服务消费者需要感知到服务地址的变化，可以通过监听服务来动态感知到服务的变化
     */
    @GetMapping("/subscribe")
    public void subscribe(@RequestParam String serviceName) throws NacosException {
        namingService.subscribe(serviceName, event -> {
            System.out.println("收到地址变更的事件");
            NamingEvent namingEvent = (NamingEvent)event;
            System.out.println(Jackson.build().writeValueAsString(namingEvent));
        });
    }

    /**
     * 取消订阅
     */
    @GetMapping("/unsubscribe")
    public void unsubscribe(@RequestParam String serviceName) throws NacosException {
        namingService.unsubscribe(serviceName, event -> {
            System.out.println("收到地址变更的事件");
            NamingEvent namingEvent = (NamingEvent)event;
            System.out.println(Jackson.build().writeValueAsString(namingEvent));
        });
    }
}