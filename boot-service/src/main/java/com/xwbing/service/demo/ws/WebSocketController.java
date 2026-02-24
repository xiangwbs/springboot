package com.xwbing.service.demo.ws;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

/**
 * @author daofeng
 * @version $
 * @since 2026年02月24日 15:55
 */
@Controller
@RequiredArgsConstructor
public class WebSocketController {
    private final SimpMessagingTemplate messagingTemplate;

    // 方式1：广播消息
    @MessageMapping("/chat")  // 客户端发送到/app/chat
//    @SendTo("/topic/messages")  // 广播到所有订阅/topic/messages的客户端
    public String broadcastMessage(String message) {
        messagingTemplate.convertAndSend("/topic/messages", message);
        return message;
    }

    // 方式2：发送给特定用户
    @MessageMapping("/private")
//    @SendToUser("/private")
    public String sendPrivateMessage(String msg, SimpMessageHeaderAccessor accessor) {
        String simpSessionId = accessor.getSessionId();
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
        String httpSessionId = (String) sessionAttributes.get("httpSessionId");
        Principal user = accessor.getUser();
        messagingTemplate.convertAndSendToUser(
                user.getName(),  // 目标用户id
                "/private",  // 客户端订阅的地址：/user/{userId}/private
                msg
        );
        return msg;
    }
}
