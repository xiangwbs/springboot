package com.xwbing.service.pay.strategy;

import javax.servlet.http.HttpServletRequest;

import com.xwbing.service.pay.dto.PayNotifyDTO;
import com.xwbing.service.pay.dto.RefundQueryRespDTO;
import com.xwbing.service.pay.dto.RefundReqDTO;
import com.xwbing.service.pay.dto.RefundRespDTO;
import com.xwbing.service.pay.dto.TradeQueryRespDTO;
import com.xwbing.service.pay.dto.TradeReqDTO;
import com.xwbing.service.pay.dto.TradeRespDTO;

/**
 * @author daofeng
 * @version $Id$
 * @since 2021年07月06日 4:51 PM
 */
public interface IPayStrategy {
    /**
     * 创建流水
     *
     * @param payReqDTO
     * @param <T>
     *
     * @return
     */
    <T> TradeRespDTO<T> createTrade(TradeReqDTO payReqDTO);

    /**
     * 退款
     *
     * @param dto
     *
     * @return
     */
    RefundRespDTO createRefund(RefundReqDTO dto);

    /**
     * 支付回调
     *
     * @param request
     * @param <T>
     *
     * @return
     */
    <T> PayNotifyDTO<T> payNotify(HttpServletRequest request);

    /**
     * 查询流水
     *
     * @param tradeNo
     * @param outTradeNo
     *
     * @return
     */
    TradeQueryRespDTO queryTrade(String tradeNo, String outTradeNo);

    /**
     * 查询退款
     *
     * @param refundNo
     * @param tradeNo
     * @param outTradeNo
     *
     * @return
     */
    RefundQueryRespDTO queryRefund(String refundNo, String tradeNo, String outTradeNo);
}