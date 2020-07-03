package com.xwbing.service.pay;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.csvreader.CsvReader;
import com.xwbing.config.constant.BaseConstant;
import com.xwbing.domain.entity.rest.AliPayBillRecord;
import com.xwbing.domain.mapper.pay.AliPayBillRecordMapper;
import com.xwbing.exception.BusinessException;
import com.xwbing.service.BaseService;
import com.xwbing.service.pay.enums.TradeTypeEnum;
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
    @Value("${spring.profiles.active:test}")
    private String env;
    private static final int BACH_SAVE_SIZE = 500;
    @Resource
    private AliPayBillRecordMapper aliPayBillRecordMapper;
    @Resource
    private AliPayBaseService aliPayBaseService;

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

    /**
     * 导入账单
     *
     * @param csv
     */
    public void loadBillByCsv(MultipartFile csv, TradeTypeEnum tradeType) {
        log.info("loadBillByCsv start");
        try {
            saveByInputStream(csv.getInputStream(), tradeType);
        } catch (IOException e) {
            log.error("loadBillByCsv error", e);
        }
        log.info("loadBillByCsv end");
    }

    /**
     * 导入账单
     */
    public void loadBill(String date, TradeTypeEnum tradeType) {
        InputStream inputStream = null;
        BufferedInputStream bis = null;
        HttpURLConnection conn = null;
        try {
            log.info("loadBill date:{} start", date);
            LocalDateTime localDateTime = DateUtil2.dateStrToLocalDateTime(date);
            Date startDate = DateUtil2.localDateTimeToDate(DateUtil2.startTimeOfDay(localDateTime));
            Date endDate = DateUtil2.localDateTimeToDate(DateUtil2.endTimeOfDay(localDateTime));
            List<AliPayBillRecord> aliPayBillRecords = listBetweenPaidDate(startDate, endDate);
            if (CollectionUtils.isNotEmpty(aliPayBillRecords)) {
                log.info("loadBill date:{} hasLoad", date);
                return;
            }
            String urlStr = aliPayBaseService.queryBillDownloadUrl(date);
            if (StringUtils.isEmpty(urlStr)) {
                throw new BusinessException("查询对账单下载地址异常");
            }
            URL url = new URL(urlStr);
            conn = (HttpURLConnection)url.openConnection();
            conn.setConnectTimeout(5 * 1000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("GET");
            conn.connect();
            inputStream = conn.getInputStream();
            ZipInputStream zin = new ZipInputStream(inputStream, Charset.forName("gbk"));
            bis = new BufferedInputStream(zin);
            byte[] bytes;
            ZipEntry ze;
            //循环读取压缩包里面的文件
            while ((ze = zin.getNextEntry()) != null) {
                if (ze.toString().endsWith("账务明细.csv")) {
                    //读取每个文件的字节，并放进数组
                    bytes = new byte[(int)ze.getSize()];
                    bis.read(bytes, 0, (int)ze.getSize());
                    //将文件转成流
                    InputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
                    saveByInputStream(byteArrayInputStream, tradeType);
                }
            }
            zin.closeEntry();
            inputStream.close();
        } catch (Exception e) {
            log.error("loadBill date:{} error", date, e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (bis != null) {
                    bis.close();
                }
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (IOException e) {
                log.error("loadBill close io error", date, e);
            }
        }
        log.info("loadBill date:{} end", date);
    }

    private void saveByInputStream(InputStream inputStream, TradeTypeEnum tradeType) {
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
                String type = reader.get(10);
                if (checkLoad(merchantOrderNo, tradeType, type)) {
                    AliPayBillRecord bill = AliPayBillRecord.builder().accountLogId(accountLogId)
                            .alipayOrderNo(reader.get(1)).merchantOrderNo(merchantOrderNo)
                            .paidDate(DateUtil2.strToDate(reader.get(4), DateUtil2.YYYY_MM_DD_HH_MM_SS))
                            .otherAccount(reader.get(5)).inAmount(new BigDecimal(reader.get(6)))
                            .outAmount(new BigDecimal(reader.get(7)).negate()).balance(new BigDecimal(reader.get(8)))
                            .type(type).remark(reader.get(11)).build();
                    list.add(bill);
                    if (list.size() >= BACH_SAVE_SIZE) {
                        super.saveBatch(list);
                        list.clear();
                    }
                }
            }
            if (list.size() != 0) {
                super.saveBatch(list);
            }
        } catch (Exception e) {
            log.error("saveByInputStream error", e);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        log.info("saveByInputStream end");
    }

    private boolean checkLoad(String merchantOrderNo, TradeTypeEnum tradeType, String type) {
        if (!tradeType.getName().equals(type)) {
            return false;
        }
        if (BaseConstant.ENV_DEV.equals(env) || BaseConstant.ENV_TEST.equals(env)) {
            return merchantOrderNo.startsWith(String.valueOf(BaseConstant.BUSINESS_LEASE_DEV)) || merchantOrderNo
                    .startsWith(String.valueOf(BaseConstant.BUSINESS_LEASE_TEST));
        } else if (BaseConstant.ENV_PRE.equals(env) || BaseConstant.ENV_PROD.equals(env)) {
            return merchantOrderNo.startsWith(String.valueOf(BaseConstant.BUSINESS_LEASE_PRE)) || merchantOrderNo
                    .startsWith(String.valueOf(BaseConstant.BUSINESS_LEASE_PROD));
        } else {
            return false;
        }
    }
}