package com.xwbing.service.demo.designpattern.template;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author xiangwb
 * @date 2020/3/6 20:22
 */
@Slf4j
@Service
public abstract class AbstractPayCallback {
    /**
     * 定义共同行为骨架
     *
     * @return
     */
    public String asynCallback() {
        //1.验签
        Map<String, String> verifySignature = verifySignature();
        //2.日志收集
        //获取验签状态
        String status = verifySignature.get("");
        if (!"success".equals(status)) {
            return resultFail();
        }
        payLog(verifySignature);
        //更改数据库状态同时返回不同支付结果
        return asyncService(verifySignature);

    }


    /**
     * 使用多线程异步写入日志 缩短响应时间
     *
     * @param verifySignature
     */
    @Async
    protected void payLog(Map<String, String> verifySignature) {
        log.info("写入数据库....verifySignature:{}", verifySignature);
    }

    /**
     * 验签
     *
     * @return
     */
    protected abstract Map<String, String> verifySignature();

    /**
     * 执行修改订单状态和返回不同的结果
     *
     * @param verifySignature
     * @return
     */
    protected abstract String asyncService(Map<String, String> verifySignature);

    /**
     * 返回失败结果
     *
     * @return
     */
    protected abstract String resultFail();

    /**
     * 返回成功结果
     *
     * @return
     */
    protected abstract String resultSuccess();
}
