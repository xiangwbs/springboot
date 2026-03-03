package com.xwbing.service.demo.ws;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.naming.AuthenticationException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author daofeng
 * @version $
 * @since 2026年02月24日 15:24
 */
@Slf4j
@Configuration
@EnableWebSocketMessageBroker
public class WsConfiguration implements WebSocketMessageBrokerConfigurer {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 配置心跳任务调度器
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(1);
        taskScheduler.setThreadNamePrefix("wss-heartbeat-thread-");
        taskScheduler.initialize();
        // 配置消息代理：/topic为广播 /user为点对点
        long heartBeat = 20000;
        registry.enableSimpleBroker("/topic", "/user").setHeartbeatValue(new long[]{heartBeat, heartBeat}).setTaskScheduler(taskScheduler);
        // 配置应用目的地前缀，会被路由到@MessageMapping注解的方法
        registry.setApplicationDestinationPrefixes("/app");
        // 配置用户目的地前缀
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // myws?userId=xxx
        registry.addEndpoint("/myws").addInterceptors(new HttpHandshakeInterceptor()).setAllowedOrigins("*").withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        MyChannelInterceptor channelInterceptor = new MyChannelInterceptor();
        registration.interceptors(channelInterceptor);
    }

    public static class HttpHandshakeInterceptor implements HandshakeInterceptor {
        @Override
        public boolean beforeHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Map<String, Object> attributes) throws Exception {
            if (serverHttpRequest instanceof ServletServerHttpRequest) {
                ServletServerHttpRequest req = (ServletServerHttpRequest) serverHttpRequest;
                HttpServletRequest servletRequest = req.getServletRequest();
                String userId = servletRequest.getParameter("userId");
                log.info("beforeHandshake userId:{}", userId);
                if (StringUtils.isEmpty(userId)) {
                    return false; // 拒绝非法连接
                }
                attributes.put("userId", userId);
                // TODO: 2026/2/28  校验是否登录
                HttpSession session = servletRequest.getSession(false);
                if (session != null) {
                    String httpSessionId = session.getId();
                    attributes.put("httpSessionId", httpSessionId);
                }
//                else {
//                    return false;
//                }
                return true;// 放行握手
            }
            return false; // 拒绝握手
        }

        @Override
        public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception e) {
            // 握手完成后
        }
    }

    public static class MyChannelInterceptor implements ChannelInterceptor {
        private static final Map<String, String> connectMap = new ConcurrentHashMap<>();

        @SneakyThrows
        @Override
        public Message<?> preSend(Message<?> message, MessageChannel channel) {
            StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
            boolean heartbeat = accessor.isHeartbeat();
            StompCommand command = accessor.getCommand();
            log.info("preSend heartbeat:{} command:{}", heartbeat, command);
            if (heartbeat || command == null) {
                return message;
            }
            Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
            String userId = (String) sessionAttributes.get("userId");
            if (userId == null) {
                throw new AuthenticationException("未获取到用户id");
            }
            switch (command) {
                case CONNECT:
                case SEND:
                case SUBSCRIBE:
                case UNSUBSCRIBE:
                    // TODO: 2026/2/28  校验是否登录
                    break;
                default:
                    break;
            }
            return message;
        }

        @Override
        public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
            if (ex != null) {
                log.error("消息发送失败", ex);
                throw new RuntimeException("websocket通道异常", ex);
            }
            StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
            boolean heartbeat = accessor.isHeartbeat();
            StompCommand command = accessor.getCommand();
            log.info("afterSendCompletion heartbeat:{} command:{}", heartbeat, command);
            if (heartbeat || command == null) {
                return;
            }
            String wsSessionId = accessor.getSessionId();
            Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
            String userId = (String) sessionAttributes.get("userId");
            switch (command) {
                case SEND:
                    break;
                case CONNECT:
                    connectHandler(userId, wsSessionId);
                    break;
                case DISCONNECT:
                    disconnectHandler(userId);
                    break;
                case SUBSCRIBE:
                case UNSUBSCRIBE:
                    String destination = (String) accessor.getHeader("simpDestination");
                    String subscriptionId = (String) accessor.getHeader("simpSubscriptionId");
                    break;
                default:
                    break;
            }
        }

        public void connectHandler(String userId, String wsSessionId) {
            connectMap.put(userId, wsSessionId);
            log.info("webSocket握手成功, userId:{}, wsSessionId:{}, 当前服务器连接数:{}", userId, wsSessionId, connectMap.size());
        }

        public void disconnectHandler(String userId) {
            log.info("webSocket断开连接, userId:{}", userId);
            connectMap.remove(userId);
        }

        public static String getWsSessionId(String userId) {
            return connectMap.get(userId);
        }
    }
}