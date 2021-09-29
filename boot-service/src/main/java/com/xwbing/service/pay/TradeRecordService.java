package com.xwbing.service.pay;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.xwbing.service.domain.entity.rest.TradeRecord;
import com.xwbing.service.domain.mapper.pay.TradeRecordMapper;
import com.xwbing.service.pay.enums.PayTypeEnum;
import com.xwbing.service.pay.enums.TradeStatusEnum;
import com.xwbing.service.service.BaseService;

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

    public TradeRecord insertPaying(String tradeNo, PayTypeEnum payType, Long amount, String subject,
            String notifyStatus) {
        TradeRecord tradeRecord = TradeRecord.builder().payType(payType).tradeNo(tradeNo).amount(amount)
                .status(TradeStatusEnum.PAYING).subject(subject).notifyStatus(notifyStatus).build();
        tradeRecordMapper.insert(tradeRecord);
        return tradeRecord;
    }

    public void updateSuccess(String tradeNo, String outTradeNo, Date paidDate, String notifyStatus, String notifyMsg) {
        TradeRecord tradeRecord = TradeRecord.builder().status(TradeStatusEnum.SUCCESS).tradeNo(tradeNo)
                .outTradeNo(outTradeNo).paidDate(paidDate).notifyStatus(notifyStatus).notifyMsg(notifyMsg).build();
        tradeRecordMapper.update(tradeRecord);
    }

    public void updateClose(String tradeNo, String outTradeNo, String code, String msg, String notifyStatus,
            String notifyMsg) {
        TradeRecord tradeRecord = TradeRecord.builder().tradeNo(tradeNo).outTradeNo(outTradeNo).code(code).msg(msg)
                .status(TradeStatusEnum.CLOSED).notifyStatus(notifyStatus).notifyMsg(notifyMsg).build();
        tradeRecordMapper.update(tradeRecord);
    }

    public void updateClosed(String tradeNo) {
        TradeRecord tradeRecord = TradeRecord.builder().tradeNo(tradeNo).status(TradeStatusEnum.CLOSED).build();
        tradeRecordMapper.update(tradeRecord);
    }

    public TradeRecord selectByTradeNo(String tradeNo, TradeStatusEnum status) {
        Map<String, Object> map = new HashMap<>();
        map.put("tradeNo", tradeNo);
        if (status != null) {
            map.put("status", status);
        }
        List<TradeRecord> tradeRecords = super.listByParam(map);
        return tradeRecords.isEmpty() ? null : tradeRecords.get(0);
    }
}
