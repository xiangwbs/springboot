package com.xwbing.web.controller.mall;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xwbing.service.mall.dto.PayNotifyDTO;
import com.xwbing.service.mall.enums.PayTypeEnum;
import com.xwbing.service.mall.strategy.PayStrategyFactory;
import com.xwbing.starter.alipay.enums.AliPayResultCodeEnum;
import com.xwbing.starter.alipay.enums.AliPayTradeStatusEnum;
import com.xwbing.starter.alipay.vo.notify.AliPayPayNotifyRequest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author xiangwb
 */
@Slf4j
@Api(tags = "payNotifyController", description = "支付异步通知")
@RestController
@RequestMapping("/pay")
@RequiredArgsConstructor
public class PayNotifyController {
    private final PayStrategyFactory payFactory;

    @ApiOperation(value = "支付宝支付异步通知")
    @PostMapping("/alipay/notify")
    public String aliPayNotify(HttpServletRequest request) {
        //验签
        PayNotifyDTO<AliPayPayNotifyRequest> notify = payFactory.getStrategy(PayTypeEnum.ALIPAY).payNotify(request);
        if (!notify.getSignValid()) {
            return AliPayResultCodeEnum.FAIL.getValue().toLowerCase();
        }
        // //业务处理
        // this.generalTradePay(alipayTradePayNotifyRequest);
        return AliPayResultCodeEnum.SUCCESS.getValue().toLowerCase();
    }

    /**
     * 支付成功业务处理
     *
     * @param request
     */
    public void generalTradePay(AliPayPayNotifyRequest request) {
        String tradeStatus = request.getTrade_status();
        if (AliPayTradeStatusEnum.TRADE_SUCCESS.getCode().equals(tradeStatus)) {
            //退款处理成功的回调，不作处理
            if (StringUtils.isNotEmpty(request.getRefund_fee()) || StringUtils.isNotEmpty(request.getGmt_refund())) {
                return;
            }
            //判断流水是否为最终状态(入账成功或退款),避免重复回调 return

            //获取商户优惠券信息
            String fundBillList = request.getFund_bill_list();
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
            //更新订单，后续业务处理
        }
    }
}
