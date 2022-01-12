package com.xwbing.starter.wx.open.app;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.google.common.collect.Maps;
import com.xwbing.starter.wx.open.app.WxOpenAppProperties.Config;

import me.chanjar.weixin.common.error.WxRuntimeException;
import me.chanjar.weixin.common.service.WxOAuth2Service;
import me.chanjar.weixin.open.api.impl.WxOpenInMemoryConfigStorage;
import me.chanjar.weixin.open.api.impl.WxOpenOAuth2ServiceImpl;

/**
 * @author daofeng
 * @version $Id$
 * @since 2021年10月05日 9:29 AM
 */
@Configuration
@EnableConfigurationProperties(WxOpenAppProperties.class)
public class WxOpenAppAutoConfiguration {
    private static final Map<String, WxOAuth2Service> OPEN_OAUTH2_MAP = Maps.newHashMap();

    public static WxOAuth2Service getOpenOAuth2Service(String appId) {
        WxOAuth2Service wxService = OPEN_OAUTH2_MAP.get(appId);
        if (wxService == null) {
            throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", appId));
        }
        return wxService;
    }

    public WxOpenAppAutoConfiguration(WxOpenAppProperties properties) {
        List<Config> configs = properties.getConfigs();
        if (CollectionUtils.isEmpty(configs)) {
            throw new WxRuntimeException("无法读取配置信息，请检查配置文件");
        }
        WxOpenOAuth2ServiceImpl wxOAuth2Service;
        for (Config config : configs) {
            wxOAuth2Service = new WxOpenOAuth2ServiceImpl(config.getAppId(), config.getSecret());
            wxOAuth2Service.setWxOpenConfigStorage(new WxOpenInMemoryConfigStorage());
            OPEN_OAUTH2_MAP.put(config.getAppId(), wxOAuth2Service);
        }
    }
}