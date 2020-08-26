package com.xwbing.service.demo.DesignPattern.template;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xiangwb
 * @date 2020/3/6 20:35
 * 在某些类的方法中，用了相同的方法，造成了代码的重复
 */
@Slf4j
public class AliPayCallbackTemplate extends AbstractPayCallback {
    @Override
    protected Map<String, String> verifySignature() {
        //假设以下为支付宝回调报文
        Map<String, String> verifySignature = new HashMap<>();
        verifySignature.put("sign", "XXXXX");
        verifySignature.put("trade_no", "123456");
        verifySignature.put("out_trade_no", "123456");
        verifySignature.put("trade_status", "TRADE_SUCCESS");
        verifySignature.put("total_amount", "12.12");
        //假设验签成功
        verifySignature.put("status", "success");
        return verifySignature;
    }

    @Override
    protected String asyncService(Map<String, String> verifySignature) {
        String tradeStatus = verifySignature.get("trade_status");
        if ("TRADE_SUCCESS".equals(tradeStatus)) {
            log.info(">>>>>已经支付成功，修改订单状态为已经支付");
        }
        return resultSuccess();
    }

    @Override
    protected String resultFail() {
        return "fail";
    }

    @Override
    protected String resultSuccess() {
        return "success";
    }


}
