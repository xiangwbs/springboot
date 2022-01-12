package com.xwbing.starter.wx.mp;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.xwbing.starter.wx.mp.handler.LogHandler;
import com.xwbing.starter.wx.mp.handler.MsgHandler;
import com.xwbing.starter.wx.mp.handler.ScanHandler;
import com.xwbing.starter.wx.mp.handler.SubscribeHandler;
import com.xwbing.starter.wx.mp.handler.UnsubscribeHandler;

import me.chanjar.weixin.common.api.WxConsts.EventType;
import me.chanjar.weixin.common.api.WxConsts.XmlMsgType;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;

/**
 * @author daofeng
 * @version $Id$
 * @since 2021年10月05日 9:29 AM
 */
@Configuration
@EnableConfigurationProperties(WxMpProperties.class)
public class WxMpAutoConfiguration {
    private final WxMpProperties properties;

    public WxMpAutoConfiguration(WxMpProperties properties) {
        this.properties = properties;
    }

    @Bean
    public WxMpService wxMpService() {
        WxMpDefaultConfigImpl config = new WxMpDefaultConfigImpl();
        config.setAppId(properties.getAppId());
        config.setSecret(properties.getSecret());
        WxMpServiceImpl service = new WxMpServiceImpl();
        service.setWxMpConfigStorage(config);
        return service;
    }

    @Bean
    public WxMpMessageRouter wxMpMessageRouter(WxMpService wxMpService) {
        final WxMpMessageRouter newRouter = new WxMpMessageRouter(wxMpService);
        // 记录所有事件的日志 （异步执行）
        newRouter.rule().handler(new LogHandler()).next();
        // 关注事件
        newRouter.rule().async(false).msgType(XmlMsgType.EVENT).event(EventType.SUBSCRIBE)
                .handler(new SubscribeHandler()).end();
        // 取消关注事件
        newRouter.rule().async(false).msgType(XmlMsgType.EVENT).event(EventType.UNSUBSCRIBE)
                .handler(new UnsubscribeHandler()).end();
        // 扫码事件
        newRouter.rule().async(false).msgType(XmlMsgType.EVENT).event(EventType.SCAN).handler(new ScanHandler()).end();
        // 默认
        newRouter.rule().async(false).handler(new MsgHandler()).end();
        return newRouter;
    }
}