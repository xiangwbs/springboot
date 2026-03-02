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
 * Socket建立后，该类用于接收用户端(/user)向服务器端(/app)发送消息，以及处理完成后服务端向用户端发送消息。
 * ========== ApplicationDestinationPrefixes("/app")的消息，服务端订阅以及客户端发送的形式
 * 服务端订阅：@MessageMapping("/broadcast")、客户端发送：stomp.send("/app/broadcast")
 * 服务端发送：@SendTo("/topic/broadcast") + return消息体、客户端订阅：stomp.subscribe("/topic/broadcast")
 * 服务端发送：simpMessagingTemplate.convertAndSend("/topic/broadcast")、客户端订阅：stomp.subscribe("/topic/broadcast")
 * <p>
 * ========== UserDestinationPrefix("/user")的消息，服务端订阅以及客户端发送的形式
 * 服务端订阅：@MessageMapping("/one")、客户端发送：stomp.send("/user/one")
 * 服务端发送：@SendToUser("/queue/one") + return消息体、客户端订阅：stomp.subscribe("/user/queue/one")
 * 服务端发送：simpMessagingTemplate.convertAndSendToUser("/queue/one")、客户端订阅：stomp.subscribe("/user/queue/one")
 *
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
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(1);
        taskScheduler.setThreadNamePrefix("wss-heartbeat-thread-");
        taskScheduler.initialize();
        //这里使用的是内存模式，生产环境可以使用rabbitmq或者其他mq。
        //这里注册两个，主要是目的是将广播和队列分开。
        long HEART_BEAT = 20000;
        registry.enableSimpleBroker("/topic", "/user").setHeartbeatValue(new long[]{HEART_BEAT, HEART_BEAT}).setTaskScheduler(taskScheduler);
        // 客户端发送消息到以/app开头的地址，会被路由到@MessageMapping注解的方法
        registry.setApplicationDestinationPrefixes("/app");
        // 指定用户发送（一对一）的前缀 /user/
        registry.setUserDestinationPrefix("/user/");
    }

    /**
     * 注册 STOMP 协议的 WebSocket 端点
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // myws?userId=xxx
        registry.addEndpoint("/myws").addInterceptors(new HttpHandshakeInterceptor()).setAllowedOrigins("*").withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        MyChannelInterceptorAdapter channelInterceptorAdapter = new MyChannelInterceptorAdapter();
        registration.interceptors(channelInterceptorAdapter);
    }

    public static class HttpHandshakeInterceptor implements HandshakeInterceptor {
        @Override
        public boolean beforeHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Map<String, Object> attributes) throws Exception {
            if (serverHttpRequest instanceof ServletServerHttpRequest) {
                ServletServerHttpRequest req = (ServletServerHttpRequest) serverHttpRequest;
                HttpServletRequest servletRequest = req.getServletRequest();
                String userId = servletRequest.getParameter("userId");
                log.info("beforeHandshake userId:{}", userId);
                if (userId == null) {
                    return false; // 拒绝非法连接
                }
                // TODO: 2026/2/28  校验是否登录
                attributes.put("userId", userId);
                HttpSession session = servletRequest.getSession(false);
                if (session != null) {
                    String httpSessionId = session.getId();
                    attributes.put("httpSessionId", httpSessionId);
                }

                return true;// 放行握手
            }
            return false; // 拒绝握手
        }

        @Override
        public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception e) {
            // 握手完成后
        }
    }

    public static class MyChannelInterceptorAdapter implements ChannelInterceptor {
        private static Map<String, String> connectMap = new ConcurrentHashMap<>();

        @SneakyThrows
        @Override
        public Message<?> preSend(Message<?> message, MessageChannel channel) {
            StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
            boolean heartbeat = accessor.isHeartbeat();
            StompCommand command = accessor.getCommand();
            log.info("preSend heartbeat:{} command:{}", heartbeat, command);
            if (heartbeat) {
                return message;
            }
            Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
            String userId = (String) sessionAttributes.get("userId");
            if (userId == null) {
                throw new AuthenticationException("未获取到用户id");
            }
            // TODO: 2026/2/28  校验是否登录
            if (StompCommand.CONNECT.equals(command)) {

            } else if (StompCommand.SEND.equals(command) || StompCommand.SUBSCRIBE.equals(command)) {
                String wsSessionId = connectMap.get(userId);
                if (StringUtils.isEmpty(wsSessionId)) {
                    throw new AuthenticationException("未连接");
                }
            }
            return message;
        }

        @Override
        public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
            if (null != ex) {
                throw new RuntimeException("websocket通道异常", ex);
            }
            StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
            boolean heartbeat = accessor.isHeartbeat();
            StompCommand command = accessor.getCommand();
            log.info("afterSendCompletion heartbeat:{} command:{}", heartbeat, command);
            if (heartbeat) {
                return;
            }
            if (null == command) {
                return;
            }
            Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
            String userId = (String) sessionAttributes.get("userId");
            String wsSessionId = accessor.getSessionId();
            switch (command) {
                case MESSAGE:
                    break;
                case CONNECT:
                    connectHandler(userId, wsSessionId);
                    break;
                case DISCONNECT:
                    disconnectHandler(userId);
                    break;
                case SUBSCRIBE:
                    break;
                case UNSUBSCRIBE:
                    break;
                case ERROR:
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

        public static String wsSessionId(String userId) {
            return connectMap.get(userId);
        }
    }
}