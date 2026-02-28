package com.xwbing.service.demo.ws;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

/**
 * ws连接地址：ws://localhost:8080/api/myws?userId=abc123
 * <p>
 * 订阅：
 * SUBSCRIBE
 * id:sub-0
 * destination:/topic/messages
 * <p>
 * \u0000
 * <p>
 * 发送消息：
 * SEND
 * destination:/app/chat
 * content-type:text/plain
 * <p>
 * Hello WebSocket!
 * ^@
 *
 * @author daofeng
 * @version $
 * @since 2026年02月24日 15:55
 */
@RestController
@RequiredArgsConstructor
public class WsController {
    private final SimpMessagingTemplate messagingTemplate;
    private final WsSendMessageToClientService wsSendMessageToClientService;

    @MessageMapping("/chat")  // 客户端发送到/app/chat
//    @SendTo("/topic/messages")  // 广播到所有订阅/topic/messages的客户端
//    @SendToUser("/private") // 指定用户发送到订阅/user/{userId}/private的客户端
    public void broadcastMessage(@Payload String message, SimpMessageHeaderAccessor accessor) {
        // 广播消息
        wsSendMessageToClientService.send(WsClientMessage.buildTopicMsg("/topic/messages", message));
//        messagingTemplate.convertAndSend("/topic/messages", message);
        // 发送给特定用户
        String simpSessionId = accessor.getSessionId();
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
        String httpSessionId = (String) sessionAttributes.get("httpSessionId");
        Principal user = accessor.getUser();
        String userId = user.getName();
        wsSendMessageToClientService.send(WsClientMessage.buildUserMsg(userId, "/private", message));
//        messagingTemplate.convertAndSendToUser(
//                simpSessionId,
//                "/private",  // 客户端订阅的地址：/user/{userId}/private
//                message
//        );
    }
}
