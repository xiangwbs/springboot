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
        instance.setClusterName("boot");
        instance.setEnabled(true);
        // AP（distro）、CP(Raft)
        instance.setEphemeral(true);
        instance.setIp("127.0.0.1");
        instance.setPort(8080);
        namingService.registerInstance("boot-module-pro", instance);
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
     */
    @GetMapping("/subscribe")
    public void subscribe(@RequestParam String serviceName) throws NacosException {
        namingService.subscribe(serviceName, event -> {
            System.out.println("收到地址变更的事件");
            NamingEvent namingEvent = (NamingEvent)event;
            System.out.println(Jackson.build().writeValueAsString(namingEvent));
        });
    }
}