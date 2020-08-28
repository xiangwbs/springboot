package com.xwbing.starter.aliyun.rocketmq;

import com.aliyun.openservices.ons.api.OnExceptionContext;
import com.aliyun.openservices.ons.api.SendCallback;
import com.aliyun.openservices.ons.api.SendResult;

import lombok.extern.slf4j.Slf4j;

/**
 * 消息者监听(订阅消费内容)
 *
 * @author daofeg
 * @version $
 * @since 2020年08月06日 20:54
 */
@Slf4j
public class DefaultSendCallback implements SendCallback {

    @Override
    public void onSuccess(SendResult sendResult) {
        log.info("消息发送成功:  topic=" + sendResult.getTopic() + ", msgId=" + sendResult.getMessageId());
    }

    @Override
    public void onException(OnExceptionContext context) {
        log.warn("消息发送失败: topic=" + context.getTopic() + ", msgId=" + context.getMessageId(), context.getException());
    }
}
