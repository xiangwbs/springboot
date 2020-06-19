package com.xwbing.handler;

import java.time.LocalDate;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.xwbing.service.pay.AliPayBillRecordService;
import com.xwbing.service.pay.enums.TradeTypeEnum;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;

/**
 * @author xiangwb
 */
@JobHandler(value = "loadBill")
@Component
public class LoadBillJobHandler extends IJobHandler {
    @Resource
    private AliPayBillRecordService aliPayBillRecordService;

    @Override
    public ReturnT<String> execute(String s) throws Exception {
        XxlJobLogger.log("loadBill start");
        aliPayBillRecordService.loadBill(LocalDate.now().minusDays(1).toString(), TradeTypeEnum.transfer);
        XxlJobLogger.log("loadBill end");
        return SUCCESS;
    }
}
