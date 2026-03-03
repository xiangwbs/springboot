package com.xwbing.service.demo.ws;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


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

    public void send(WsClientMessage wsClientMessage) {
        stringRedisTemplate.convertAndSend(getChannel(), JSONUtil.toJsonStr(wsClientMessage));
    }

    public String getChannel() {
        return "myws:message:" + this.environment;
    }

    public boolean hasConnect(String userId) {
        String connectTime = (String) stringRedisTemplate.opsForHash().get(WsConfiguration.MyChannelInterceptor.CONNECT_KEEP_ALIVE, userId);
        if (connectTime != null) {
            long diffTime = System.currentTimeMillis() - NumberUtil.parseLong(connectTime);
            return diffTime <= WsConfiguration.HEART_BEAT;
        }
        return false;
    }

    public List<String> hasConnect(List<String> userIds) {
        List<Object> connects = stringRedisTemplate.opsForHash().multiGet(WsConfiguration.MyChannelInterceptor.CONNECT_KEEP_ALIVE, new ArrayList<>(userIds));
        List<String> connectList = new ArrayList<>();
        for (int i = 0; i < userIds.size(); i++) {
            String userId = userIds.get(i);
            Object connectTime = connects.get(i);
            if (connectTime != null) {
                long diffTime = System.currentTimeMillis() - NumberUtil.parseLong((String) connectTime);
                if (diffTime <= WsConfiguration.HEART_BEAT) {
                    connectList.add(userId);
                }
            }
        }
        return connectList;
    }
}