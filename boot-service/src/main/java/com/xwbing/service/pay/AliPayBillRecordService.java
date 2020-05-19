package com.xwbing.service.pay;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.csvreader.CsvReader;
import com.xwbing.domain.entity.rest.AliPayBillRecord;
import com.xwbing.domain.mapper.rest.AliPayBillRecordMapper;
import com.xwbing.service.BaseService;
import com.xwbing.util.DateUtil2;

import lombok.extern.slf4j.Slf4j;

/**
 * @author daofeng
 * @version $Id$
 * @since 2020年05月19日 下午3:15
 */
@Slf4j
@Service
@PropertySource("classpath:pay.properties")
public class AliPayBillRecordService extends BaseService<AliPayBillRecordMapper, AliPayBillRecord> {
    @Resource
    private AliPayBillRecordMapper aliPayBillRecordMapper;

    @Override
    protected AliPayBillRecordMapper getMapper() {
        return aliPayBillRecordMapper;
    }

    public List<AliPayBillRecord> listBetweenPaidDate(Date startDate, Date endDate) {
        Map<String, Object> map = new HashMap<>(2);
        map.put("startDate", startDate);
        map.put("endDate", endDate);
        return super.listByParam(map);
    }

    public void saveByInputStream(InputStream inputStream) {
        log.info("saveByInputStream start");
        CsvReader reader = null;
        try {
            //如果生产文件乱码，windows下用gbk，linux用UTF-8
            reader = new CsvReader(inputStream, ',', Charset.forName("gbk"));
            List<AliPayBillRecord> list = new ArrayList<>();
            while (reader.readRecord()) {
                String accountLogId = reader.get(0);
                if (accountLogId.contains("#") || "账务流水号".equals(accountLogId)) {
                    continue;
                }
                String merchantOrderNo = reader.get(2);
                AliPayBillRecord bill = AliPayBillRecord.builder().accountLogId(accountLogId)
                        .alipayOrderNo(reader.get(1)).merchantOrderNo(merchantOrderNo)
                        .paidDate(DateUtil2.strToDate(reader.get(4), DateUtil2.YYYY_MM_DD_HH_MM_SS))
                        .otherAccount(reader.get(5)).inAmount(new BigDecimal(reader.get(6)))
                        .outAmount(new BigDecimal(reader.get(7)).negate()).balance(new BigDecimal(reader.get(8)))
                        .type(reader.get(10)).remark(reader.get(11)).build();
                list.add(bill);
                if (list.size() >= 500) {
                    super.saveBatch(list);
                    list.clear();
                }
            }
            super.saveBatch(list);
        } catch (Exception e) {
            log.error("saveByInputStream error", e);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        log.info("saveByInputStream end");
    }
}
