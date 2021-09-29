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
     *
     * @return
     */
    TradeRespDTO createTrade(TradeReqDTO payReqDTO);

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
     * 支付回调
     *
     * @param <T>
     * @param request
     *
     * @return
     */
    <T> PayNotifyDTO payNotify(HttpServletRequest request);

    /**
     * 退款
     *
     * @param dto
     *
     * @return
     */
    RefundRespDTO createRefund(RefundReqDTO dto);

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

    /**
     * 撤销流水
     *
     * @param tradeNo
     * @param outTradeNo
     *
     * @return
     */
    boolean cancelTrade(String tradeNo, String outTradeNo);
}