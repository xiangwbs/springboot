package com.xwbing.service.pay;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.xwbing.domain.entity.rest.TradeRecord;
import com.xwbing.domain.mapper.rest.TradeRecordMapper;
import com.xwbing.service.BaseService;
import com.xwbing.service.pay.enums.PayStatusEnum;

import lombok.extern.slf4j.Slf4j;

/**
 * @author daofeng
 * @version $Id$
 * @since 2020年05月19日 下午4:36
 */
@Service
@Slf4j
public class TradeRecordService extends BaseService<TradeRecordMapper, TradeRecord> {
    @Resource
    private TradeRecordMapper tradeRecordMapper;

    @Override
    protected TradeRecordMapper getMapper() {
        return tradeRecordMapper;
    }

    public TradeRecord insertPaying(String payType, String tradeNo, Long amount, String subject, String notifyStatus) {
        TradeRecord tradeRecord = TradeRecord.builder().payType(payType).tradeNo(tradeNo).amount(amount)
                .status(PayStatusEnum.PAYING.getCode()).subject(subject).notifyStatus(notifyStatus).build();
        tradeRecordMapper.insert(tradeRecord);
        return tradeRecord;
    }

    public void updateSuccess(String id, String outTradeNo, Date paidDate, String notifyStatus, String notifyMsg) {
        TradeRecord tradeRecord = TradeRecord.builder().id(id).
                status(PayStatusEnum.SUCCESS.getCode()).outTradeNo(outTradeNo).paidDate(paidDate)
                .notifyStatus(notifyStatus).notifyMsg(notifyMsg).build();
        tradeRecordMapper.update(tradeRecord);
    }

    public void updateFail(String id, String outTradeNo, String code, String msg, String subCode, String subMsg,
            String notifyStatus, String notifyMsg) {
        TradeRecord tradeRecord = TradeRecord.builder().id(id).code(code).msg(msg).subCode(subCode).subMsg(subMsg)
                .status(PayStatusEnum.FAIL.getCode()).outTradeNo(outTradeNo).notifyStatus(notifyStatus)
                .notifyMsg(notifyMsg).build();
        tradeRecordMapper.update(tradeRecord);
    }

    public void updateClosed(String id) {
        TradeRecord tradeRecord = TradeRecord.builder().id(id).status(PayStatusEnum.CLOSED.getCode()).build();
        tradeRecordMapper.update(tradeRecord);
    }
}
