package com.xwbing.service.demo.ws;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * ws连接地址：ws://localhost:8080/api/myws?userId=abc123
 *
 * @author daofeng
 * @version $
 * @since 2026年02月24日 15:55
 */
@RestController
@RequestMapping("/ws")
@RequiredArgsConstructor
public class WsController {
    private final SimpMessagingTemplate messagingTemplate;
    private final WsSendMessageToClientService wsSendMessageToClientService;

    @GetMapping("/hasConnect")
    public boolean hasConnect(@RequestParam String userId) {
        return wsSendMessageToClientService.hasConnect(userId);
    }

    @GetMapping("/hasConnectList")
    public List<String> hasConnectList(@RequestParam List<String> userIds) {
        return wsSendMessageToClientService.hasConnect(userIds);
    }

    @MessageMapping("/chat")  // 客户端发送到/app/chat
//    @SendTo("/topic/messages")  // 广播到所有订阅/topic/messages的客户端
//    @SendToUser("/private") // 指定用户发送到订阅/user/{userName}/private的客户端:SimpMessageHeaderAccessor.getUser=Principal principal.getName()
    public void broadcastMessage(@Payload String message, SimpMessageHeaderAccessor accessor) {
        // 广播消息
        wsSendMessageToClientService.send(WsClientMessage.buildTopicMsg("/topic/messages", "服务端广播消息：" + message));
//        messagingTemplate.convertAndSend("/topic/messages", message);
        // 发送给特定用户
        String wsSessionId = accessor.getSessionId();
        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
        String httpSessionId = (String) sessionAttributes.get("httpSessionId");
        String userId = (String) sessionAttributes.get("userId");
        // 客户端订阅的地址：/user/{userId}/private
        wsSendMessageToClientService.send(WsClientMessage.buildUserMsg(userId, "/private", "服务端私发消息：" + message));
//        messagingTemplate.convertAndSendToUser(userId, "/private", message);
    }
}
