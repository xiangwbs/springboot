package com.xwbing.service.demo.dingtalk;

import com.dingtalk.api.DefaultDingTalkClient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author daofeng
 * @version $Id$
 * @since 2023年01月11日 9:29 AM
 */
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class DingtalkRobotMsg {
    private String msgId;
    private DefaultDingTalkClient client;
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
     * 消息的时间戳 单位ms
     */
    private String createAt;
    /**
     * 机器人应用的appKey
     */
    private String robotCode;
    private String content;
}