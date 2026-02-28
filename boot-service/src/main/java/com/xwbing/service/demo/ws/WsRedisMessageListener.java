package com.xwbing.service.demo.ws;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * @author ligangyan
 * @version $   ;p
 * @since 5月 28, 2021 12:14 下午
 */
@Component
@Slf4j
public class WsRedisMessageListener implements MessageListener {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public void onMessage(Message message, byte[] bytes) {
        // 监听redis消息  集群模式
        RedisSerializer<String> valueSerializer = stringRedisTemplate.getStringSerializer();
        String value = valueSerializer.deserialize(message.getBody());
        if (StringUtils.isNotEmpty(value)) {
            WsClientMessage wsClientMessage = JSONUtil.toBean(value, WsClientMessage.class);
            if (wsClientMessage.getMsgType() == WsClientMessage.MsgType.USER_MSG) {
                String userId = wsClientMessage.getUserId();
                String wsSessionId = WsConfiguration.MyChannelInterceptorAdapter.wsSessionId(userId);
                if (StringUtils.isNotEmpty(wsSessionId)) {
                    simpMessagingTemplate.convertAndSendToUser(userId, wsClientMessage.getDestination(), wsClientMessage.getMessage());
                }
            } else if (wsClientMessage.getMsgType() == WsClientMessage.MsgType.TOPIC_MSG) {
                simpMessagingTemplate.convertAndSend(wsClientMessage.getDestination(), wsClientMessage.getMessage());
            }
        }
    }
}