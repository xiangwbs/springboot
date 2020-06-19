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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.csvreader.CsvReader;
import com.xwbing.config.constant.BaseConstant;
import com.xwbing.domain.entity.rest.AliPayBillRecord;
import com.xwbing.domain.mapper.pay.AliPayBillRecordMapper;
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
    @Value("${spring.profiles.active:test}")
    private String env;
    private static final int BACH_SAVE_SIZE = 500;
    @Resource
    private AliPayBillRecordMapper aliPayBillRecordMapper;
    @Resource
    private AliPayTransferService aliPayTransferService;

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
    public void loadBillByCsv(MultipartFile csv) {
        log.info("loadBillByCsv start");
        try {
            saveByInputStream(csv.getInputStream());
        } catch (IOException e) {
            log.error("loadBillByCsv error", e);
        }
        log.info("loadBillByCsv end");
    }

    /**
     * 导入账单
     */
    public void loadBill(String date) {
        log.info("loadBill date:{} start", date);
        try {
            LocalDateTime localDateTime = DateUtil2.dateStrToLocalDateTime(date);
            Date startDate = DateUtil2.localDateTimeToDate(DateUtil2.startTimeOfDay(localDateTime));
            Date endDate = DateUtil2.localDateTimeToDate(DateUtil2.endTimeOfDay(localDateTime));
            List<AliPayBillRecord> aliPayBillRecords = listBetweenPaidDate(startDate, endDate);
            if (CollectionUtils.isNotEmpty(aliPayBillRecords)) {
                log.info("loadBill date:{} hasLoad", date);
                return;
            }
            String urlStr = aliPayTransferService.queryBillDownloadUrl(date);
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            //设置超时间为3秒
            conn.setConnectTimeout(3 * 1000);
            //得到输入流
            InputStream inputStream = conn.getInputStream();
            ZipInputStream zin = new ZipInputStream(inputStream, Charset.forName("gbk"));
            BufferedInputStream bs = new BufferedInputStream(zin);
            byte[] bytes;
            ZipEntry ze;
            //循环读取压缩包里面的文件
            while ((ze = zin.getNextEntry()) != null) {
                if (ze.toString().endsWith("账务明细.csv")) {
                    //读取每个文件的字节，并放进数组
                    bytes = new byte[(int)ze.getSize()];
                    bs.read(bytes, 0, (int)ze.getSize());
                    //将文件转成流
                    InputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
                    saveByInputStream(byteArrayInputStream);
                }
            }
            zin.closeEntry();
            inputStream.close();
        } catch (Exception e) {
            log.error("loadBill date:{} error", date, e);
        }
        log.info("loadBill date:{} end", date);
    }

    private void saveByInputStream(InputStream inputStream) {
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
                if (checkLoad(merchantOrderNo, type)) {
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

    private boolean checkLoad(String merchantOrderNo, String type) {
        if (!"转账".equals(type)) {
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