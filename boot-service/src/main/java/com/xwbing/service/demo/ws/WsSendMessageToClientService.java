package com.xwbing.service.demo.ws;

import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;


/**
 * 客服系统推送消息至websocket客户端服务
 *
 * @author ligangyan
 * @version $
 * @since 5月 28, 2021 12:01 下午
 */
@Component
public class WsSendMessageToClientService {

    @Value("${spring.profiles.active}")
    private String environment;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * @param wsClientMessage
     */
    public void send(WsClientMessage wsClientMessage) {
        stringRedisTemplate.convertAndSend(getChannel(), JSONUtil.toJsonStr(wsClientMessage));
    }

    public String getChannel() {
        return this.environment + "_qyhc_manualWebsocket";
    }

}
