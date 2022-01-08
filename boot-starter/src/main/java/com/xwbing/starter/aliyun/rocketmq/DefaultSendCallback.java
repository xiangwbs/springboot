package com.xwbing.starter.aliyun.rocketmq;

import com.aliyun.openservices.ons.api.OnExceptionContext;
import com.aliyun.openservices.ons.api.SendCallback;
import com.aliyun.openservices.ons.api.SendResult;

import lombok.extern.slf4j.Slf4j;

/**
 * 异步发送默认回调接口
 *
 * @author daofeg
 * @version $
 * @since 2020年08月06日 20:54
 */
@Slf4j
public class DefaultSendCallback implements SendCallback {
    @Override
    public void onSuccess(SendResult sendResult) {
        log.info("sendCallback onSuccess topic:{} msgId:{}", sendResult.getTopic(), sendResult.getMessageId());
    }

    @Override
    public void onException(OnExceptionContext context) {
        log.error("sendCallback onException topic:{} msgId:{}", context.getTopic(), context.getMessageId());
    }
}