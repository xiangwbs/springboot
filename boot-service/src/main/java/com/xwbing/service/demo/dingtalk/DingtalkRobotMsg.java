package com.xwbing.service.demo.dingtalk;

import com.dingtalk.api.DefaultDingTalkClient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author daofeng
 * @version $Id$
 * @see <a href="https://open.dingtalk.com/document/robots/receive-message">接收消息的消息协议</a>
 * @since 2023年01月11日 9:29 AM
 */
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class DingtalkRobotMsg {
    /**
     * 加密的消息ID
     */
    private String msgId;
    /**
     * 加密的机器人ID
     */
    private String chatbotUserId;
    /**
     * 加密的机器人所在的企业corpId
     */
    private String chatbotCorpId;
    /**
     * 企业内部群有的发送者当前群的企业corpId
     */
    private String senderCorpId;
    /**
     * 企业内部群中@该机器人的成员userId
     */
    private String senderStaffId;
    /**
     * 加密的发送者ID
     */
    private String senderId;
    /**
     * 发送者昵称
     */
    private String senderNick;
    /**
     * 1=单聊 2=群聊
     */
    private Integer conversationType;
    /**
     * 加密的会话ID
     */
    private String conversationId;
    /**
     * 消息的时间戳 单位ms
     */
    private String createAt;
    /**
     * 机器人应用的appKey
     */
    private String robotCode;

    private DefaultDingTalkClient client;
    private String content;
}