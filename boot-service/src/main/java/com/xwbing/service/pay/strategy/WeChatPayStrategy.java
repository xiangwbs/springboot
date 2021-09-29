package com.xwbing.service.pay.strategy;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.xwbing.service.pay.dto.PayNotifyDTO;
import com.xwbing.service.pay.dto.RefundQueryRespDTO;
import com.xwbing.service.pay.dto.RefundReqDTO;
import com.xwbing.service.pay.dto.RefundRespDTO;
import com.xwbing.service.pay.dto.TradeQueryRespDTO;
import com.xwbing.service.pay.dto.TradeReqDTO;
import com.xwbing.service.pay.dto.TradeRespDTO;

import lombok.extern.slf4j.Slf4j;

/**
 * @author daofeng
 * @version $Id$
 * @since 2021年07月06日 4:52 PM
 */
@Slf4j
@Service
public class WeChatPayStrategy implements IPayStrategy {

    @Override
    public TradeRespDTO createTrade(TradeReqDTO payReqDTO) {
        return null;
    }

    @Override
    public TradeQueryRespDTO queryTrade(String tradeNo, String outTradeNo) {
        return null;
    }

    @Override
    public <T> PayNotifyDTO payNotify(HttpServletRequest request) {
        return null;
    }

    @Override
    public RefundRespDTO createRefund(RefundReqDTO dto) {
        return null;
    }

    @Override
    public RefundQueryRespDTO queryRefund(String refundNo, String tradeNo, String outTradeNo) {
        return null;
    }

    @Override
    public boolean cancelTrade(String tradeNo, String outTradeNo) {
        return false;
    }
}
