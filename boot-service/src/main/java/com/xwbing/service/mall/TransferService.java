package com.xwbing.service.mall;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.xwbing.service.domain.entity.rest.TradeRecord;
import com.xwbing.starter.alipay.AliPayHelper;
import com.xwbing.service.mall.enums.TradeStatusEnum;
import com.xwbing.service.mall.enums.PayTypeEnum;
import com.xwbing.starter.alipay.vo.request.AliPayTransferParam;
import com.xwbing.starter.alipay.vo.response.AliPayTransferQueryResult;
import com.xwbing.starter.alipay.vo.response.AliPayTransferResult;
import com.xwbing.service.util.DateUtil2;
import com.xwbing.service.util.DecimalUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 转账到支付宝账户接口
 *
 * 转账额度
 * 单笔限额：转账给个人支付宝账户，单笔最高 5 万元；转账给企业支付宝账户，单笔最高 10 万元。
 * 日限额：初始额度为 200 万元，即每日最高可转200万元。
 * 月限额：初始额度为 3100 万元，即每月最高可转3100万元。
 *
 * 相关文档
 * https://opendocs.alipay.com/open/309/106235
 * https://opensupport.alipay.com/support/helpcenter/107/201602484934#?ant_source=manual&recommend=84921dbf0458195602513447ca3ca661
 *
 * @author daofeng
 * @version $Id$
 * @since 2020年04月29日 下午12:08
 */
@Slf4j
@Service
public class TransferService {
    @Resource
    private TradeRecordService tradeRecordService;
    @Resource
    private AliPayHelper aliPayTradeService;

    /**
     * 转账
     *
     * @param param
     *
     * @return
     */
    public void doTransfer(AliPayTransferParam param) {
        //@formatter:off
        String tradeNo = param.getOutBizNo();
        //查询支付中流水
        TradeRecord tradeRecord = tradeRecordService.selectByTradeNo(tradeNo, TradeStatusEnum.PAYING);
        if (tradeRecord != null) {
            return;
        }
        //添加支付中流水
        tradeRecordService.insertPaying(tradeNo, PayTypeEnum.ALIPAY, DecimalUtil.toFen(param.getAmount()), param.getTitle(), null);
        //转账
        AliPayTransferResult transfer = aliPayTradeService.transfer(param);
        if (!"unknow-error".equals(transfer.getCode())) {
            if (transfer.isSuccess()) {
                tradeRecordService.updateSuccess(tradeNo, transfer.getOrderId(), DateUtil2.strToDate(transfer.getTransDate(), DateUtil2.YYYY_MM_DD_HH_MM_SS), null, null);
            } else {
                tradeRecordService.updateClose(tradeNo, transfer.getOrderId(), transfer.getCode(), transfer.getMessage(), null, null);
            }
            return;
        }
        //查询转账信息
        AliPayTransferQueryResult query = aliPayTradeService.transferQuery(tradeNo);
        //支付宝接口异常
        if ("unknow-error".equals(query.getCode())) {
            tradeRecordService.updateClose(tradeNo, query.getOrderId(), query.getCode(), query.getMessage(), null, null);
            return;
        }
        if (query.isSuccess()) {
            tradeRecordService.updateSuccess(tradeNo, query.getOrderId(), DateUtil2.strToDate(query.getPayDate(), DateUtil2.YYYY_MM_DD_HH_MM_SS), null, null);
        } else {
            //再次转账
            transfer = aliPayTradeService.transfer(param);
            if (transfer.isSuccess()) {
                tradeRecordService.updateSuccess(tradeNo, transfer.getOrderId(), DateUtil2.strToDate(transfer.getTransDate(), DateUtil2.YYYY_MM_DD_HH_MM_SS), null, null);
            } else {
                tradeRecordService.updateClose(tradeNo, transfer.getOrderId(), transfer.getCode(), transfer.getMessage(), null, null);
            }
        }
    }
}