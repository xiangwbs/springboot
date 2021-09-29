package com.xwbing.service.pay.strategy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.xwbing.service.pay.dto.PayNotifyDTO;
import com.xwbing.service.pay.dto.RefundQueryRespDTO;
import com.xwbing.service.pay.dto.RefundReqDTO;
import com.xwbing.service.pay.dto.RefundRespDTO;
import com.xwbing.service.pay.dto.TradeQueryRespDTO;
import com.xwbing.service.pay.dto.TradeReqDTO;
import com.xwbing.service.pay.dto.TradeRespDTO;
import com.xwbing.service.pay.enums.PayWayEnum;
import com.xwbing.service.pay.enums.TradeStatusEnum;
import com.xwbing.service.util.DecimalUtil;
import com.xwbing.service.util.Jackson;
import com.xwbing.starter.alipay.AliPayHelper;
import com.xwbing.starter.alipay.enums.AliPayTradeStatusEnum;
import com.xwbing.starter.alipay.vo.notify.AliPayPayNotifyRequest;
import com.xwbing.starter.alipay.vo.request.AliPayAppPayParam;
import com.xwbing.starter.alipay.vo.request.AliPayPagePayParam;
import com.xwbing.starter.alipay.vo.request.AliPayTradeCreateParam;
import com.xwbing.starter.alipay.vo.request.AliPayTradePayParam;
import com.xwbing.starter.alipay.vo.request.AliPayTradePreCreateParam;
import com.xwbing.starter.alipay.vo.request.AliPayTradeRefundParam;
import com.xwbing.starter.alipay.vo.request.AliPayWapPayParam;
import com.xwbing.starter.alipay.vo.response.AliPayRefundQueryResult;
import com.xwbing.starter.alipay.vo.response.AliPayTradeQueryResult;
import com.xwbing.starter.alipay.vo.response.AliPayTradeRefundResult;
import com.xwbing.starter.exception.PayException;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.date.DatePattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author daofeng
 * @version $Id$
 * @since 2021年07月06日 4:52 PM
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AliPayStrategy implements IPayStrategy {
    @Value("${server.servlet.context-path}")
    private String path;
    @Value("${boot.host}")
    private String host;
    @Value("${boot.appHost}")
    private String appHost;
    private final AliPayHelper aliPayHelper;

    @Override
    public <T> TradeRespDTO<T> createTrade(TradeReqDTO dto) {
        PayWayEnum payWay = dto.getPayWay();
        String tradeNo = dto.getTradeNo();
        String subject = dto.getSubject();
        BigDecimal totalAmount = DecimalUtil.toYuan(dto.getTotalAmount());
        String notifyUrl = host + path + aliPayHelper.getProperties().getNotifyUrl();
        String returnUrl = appHost + String.format(aliPayHelper.getProperties().getReturnUrl(), dto.getOrderNo());
        String quitUrl = appHost + String.format(aliPayHelper.getProperties().getQuitUrl(), dto.getOrderNo());
        Object resp;
        switch (payWay) {
            case MOBILE:
                AliPayWapPayParam wapPay = AliPayWapPayParam
                        .of(tradeNo, subject, totalAmount, returnUrl, quitUrl, notifyUrl);
                resp = aliPayHelper.wapPay(wapPay);
                break;
            case PC:
                AliPayPagePayParam pagePay = AliPayPagePayParam.of(tradeNo, subject, totalAmount, returnUrl, notifyUrl);
                resp = aliPayHelper.pagePay(pagePay);
                break;
            case APP:
                AliPayAppPayParam appPay = AliPayAppPayParam.of(tradeNo, subject, totalAmount, notifyUrl);
                resp = aliPayHelper.appPay(appPay);
                break;
            case SCAN_CODE:
                AliPayTradePreCreateParam preCreate = AliPayTradePreCreateParam
                        .of(tradeNo, subject, totalAmount, notifyUrl);
                resp = aliPayHelper.tradePreCreate(preCreate);
                break;
            case AUTH_CODE:
                AliPayTradePayParam tradePay = AliPayTradePayParam
                        .bar(tradeNo, dto.getAuthCode(), subject, totalAmount, notifyUrl);
                resp = aliPayHelper.tradePay(tradePay);
                break;
            case MINI_PROGRAM:
                AliPayTradeCreateParam tradeCreate = AliPayTradeCreateParam
                        .of(tradeNo, dto.getAliPayBuyerId(), subject, totalAmount, notifyUrl);
                resp = aliPayHelper.tradeCreate(tradeCreate);
                break;
            default:
                throw new PayException("该交易类型暂不支持");
        }
        //@formatter:off
        TradeRespDTO payRespDTO = TradeRespDTO.builder()
                .orderNo(dto.getOrderNo())
                .tradeNo(dto.getTradeNo())
                .tradeStatus(TradeStatusEnum.PAYING)
                .desc(dto.getSubject())
                .resp(resp).build();
        //@formatter:on
        return payRespDTO;
    }

    @Override
    public RefundRespDTO createRefund(RefundReqDTO dto) {
        log.info("aliPay createRefund tradeNo:{} dto", dto.getTradeNo(), dto);
        AliPayTradeRefundParam param = AliPayTradeRefundParam
                .of(dto.getRefundNo(), dto.getOutTradeNo(), dto.getTradeNo(), DecimalUtil.toYuan(dto.getRefundAmount()),
                        dto.getRefundReason());
        AliPayTradeRefundResult result = aliPayHelper.tradeRefund(param);
        log.info("aliPay createRefund tradeNo:{} result", dto.getTradeNo(), result);
        return result.isSuccess() ? RefundRespDTO.ofSuccess(result.getRefundTime()) : RefundRespDTO.ofFail();
    }

    @Override
    public <T> PayNotifyDTO<T> payNotify(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        log.info("aliPay notify request:{}", Jackson.build().writeValueAsString(parameterMap));
        Map<String, String> paramMap = new HashMap<>(parameterMap.size());
        parameterMap.forEach((k, vs) -> {
            if (vs.length == 1) {
                paramMap.put(k, vs[0]);
            } else if (vs.length > 1) {
                paramMap.put(k, Arrays.stream(vs).collect(Collectors.joining(",")));
            }
        });
        String tradeNo = paramMap.get("out_trade_no");
        boolean checkSign;
        try {
            checkSign = AlipaySignature.rsaCertCheckV1(paramMap, aliPayHelper.getProperties().getAliPayPublicCertPath(),
                    aliPayHelper.getProperties().getCharset(), aliPayHelper.getProperties().getSignType());
        } catch (AlipayApiException e) {
            log.error("aliPay notify tradeNo:{} checkSign error", tradeNo, e);
            checkSign = false;
        }
        if (!checkSign) {
            log.error("aliPay notify tradeNo:{} checkSign failed", tradeNo);
        }
        AliPayPayNotifyRequest notifyInfo = BeanUtil
                .mapToBean(paramMap, AliPayPayNotifyRequest.class, CopyOptions.create().ignoreNullValue());
        AliPayTradeStatusEnum aliPayTradeStatus = AliPayTradeStatusEnum.parse(notifyInfo.getTrade_status());
        //@formatter:off
        PayNotifyDTO notify = PayNotifyDTO
                .builder()
                .outTradeNo(notifyInfo.getTrade_no())
                .tradeNo(notifyInfo.getOut_trade_no())
                .totalAmount(DecimalUtil.toFen(new BigDecimal(notifyInfo.getTotal_amount())))
                .tradeStatus(TradeStatusEnum.of(aliPayTradeStatus))
                .paidTime((StringUtils.isEmpty(notifyInfo.getGmt_payment()) ? null : LocalDateTime.parse(notifyInfo.getGmt_payment(), DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN))))
                .notifyInfo(notifyInfo)
                .signValid(checkSign)
                .build();
        //@formatter:on
        log.info("aliPay notify tradeNo:{} response:{}", tradeNo, notify);
        return notify;
    }

    @Override
    public TradeQueryRespDTO queryTrade(String tradeNo, String outTradeNo) {
        AliPayTradeQueryResult result = aliPayHelper.tradeQuery(tradeNo, outTradeNo);
        return result.isSuccess() ? TradeQueryRespDTO.of(result) : null;
    }

    @Override
    public RefundQueryRespDTO queryRefund(String refundNo, String tradeNo, String outTradeNo) {
        AliPayRefundQueryResult result = aliPayHelper.refundQuery(refundNo, tradeNo, outTradeNo);
        return result.isSuccess() ? RefundQueryRespDTO.of(result) : null;
    }
}