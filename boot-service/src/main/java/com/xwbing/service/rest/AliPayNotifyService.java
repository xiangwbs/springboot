package com.xwbing.service.rest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.xwbing.domain.entity.pay.alipay.AliPayTradePayNotifyRequest;
import com.xwbing.domain.entity.pay.alipay.AliPayTradeStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.Map;

/**
 * @author xiangwb
 * @date 20/2/9 16:11
 * 支付宝异步通知处理
 */
@Slf4j
@Service
public class AliPayNotifyService {
    @Value("${aliPay.rsaPublicKey}")
    private String publicKey;

    /**
     * 验签
     *
     * @param aliPayTradePayNotifyRequest
     */
    public void verifyTradePayParam(AliPayTradePayNotifyRequest aliPayTradePayNotifyRequest) {
        String outTradeNo = aliPayTradePayNotifyRequest.getOut_trade_no();
        try {
            Map<String, String> params = JSONObject.parseObject(JSONObject.toJSONString(aliPayTradePayNotifyRequest), new TypeReference<Map<String, String>>() {
            });
            //去除无效参数
            Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> next = iterator.next();
                String value = next.getValue();
                if (StringUtils.isBlank(value)) {
                    iterator.remove();
                }
            }
            //验签
            boolean flag = AlipaySignature.rsaCheckV1(params, publicKey, "utf-8", aliPayTradePayNotifyRequest.getSign_type());
            if (!flag) {
                log.error("verifyTradeCreateParams {} 验签失败", outTradeNo);
            }
        } catch (AlipayApiException e) {
            log.error("verifyTradeCreateParams {} exception:{}", outTradeNo, ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 支付成功业务处理
     *
     * @param aliPayTradePayNotifyRequest
     */
    public void generalTradePay(AliPayTradePayNotifyRequest aliPayTradePayNotifyRequest) {
        String tradeStatus = aliPayTradePayNotifyRequest.getTrade_status();
        if (AliPayTradeStatusEnum.TRADE_SUCCESS.getCode().equals(tradeStatus)) {
            //判断流水是否为最终状态(入账成功或退款),避免重复回调 return
            //获取商户优惠券信息
            String fundBillList = aliPayTradePayNotifyRequest.getFund_bill_list();
            if (StringUtils.isNotEmpty(fundBillList)) {
                JSONArray.parseArray(fundBillList).stream().map(o -> JSONObject.parseObject(JSONObject.toJSONString(o)))
                        .filter(object -> "MDISCOUNT".equals(object.getString("fundChannel"))).findFirst()
                        .ifPresent(object -> {
                            //用于流水和订单金额减免
                            String discount = object.getString("amount");
                        });

            }
            //更新流水
            //检查总成功流水金额是否大于订单金额,重复支付提醒
            //更新订单
            //后续业务处理
        }
    }
}