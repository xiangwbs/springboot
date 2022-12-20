package com.xwbing.service.demo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.cache.MapCache;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.fastjson.JSONObject;
import com.csvreader.CsvReader;

import lombok.extern.slf4j.Slf4j;

/**
 * 创建日期: 2017年2月21日 上午10:12:20
 * 作者: xiangwb
 */
@Slf4j
public class IODemo {
    public static void main(String[] args) throws Exception {
        /**
         * GBK：国标编码，中文占2字节
         * UTF-8：unicode的子集，其中文占3个字节
         * ISO8859-1：欧洲编码集，不支持中文
         */

        /**
         * 字节流
         */
        /* 写 */
        FileOutputStream fos = new FileOutputStream("io.txt", true);//追加写模式
        fos.write("字节流".getBytes("utf-8"));
        System.out.println("写出完毕");
        fos.close();//关闭流
        /* 读 */
        FileInputStream fis = new FileInputStream("io.txt");
        byte[] data = new byte[fis.available()];
        int len = fis.read(data);//数据读入data里
        String str = new String(data, 0, len, "utf-8");
        fis.close();

        InputStream is = new ByteArrayInputStream(str.getBytes("utf-8"));
        byte[] da = new byte[is.available()];
        is.read(da);
        is.close();

        /**
         * 缓冲流
         */
        /* 写 */
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("io.txt", true));
        bos.write("缓冲流".getBytes());
        bos.close();//close前会自动flush，关流只需管最外层高级流
        /* 使用缓冲流的形式复制文件 */
        BufferedInputStream bis1 = new BufferedInputStream(new FileInputStream("io.txt"));
        BufferedOutputStream bos1 = new BufferedOutputStream(new FileOutputStream("io.txt", true));
        int d;
        while ((d = bis1.read()) != -1) {
            bos1.write(d);
        }
        bis1.close();
        bos1.close();

        /**
         * 字符流
         * 读写单位是字符
         * 可以按照指定的字符集，将写出的字符转换为对应的字节
         * 字符流只适合读写文本数据
         * 转码:uft-8转gbk
         */
        InputStreamReader isr = new InputStreamReader(new FileInputStream("io.txt"), "utf-8");
        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream("io.tex"), "gbk");
        int length;
        while ((length = isr.read()) != -1) {
            osw.write(length);
        }
        osw.close();
        isr.close();

        /**
         * 缓冲字符输入流，特点：读取速度快，并且可以按行读取字符串
         */
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("io.txt")));
        String line = null;
        //readerLIne():连续读取若干字符，知道遇到换行符为止,该字符串中不包含换行符
        while ((line = br.readLine()) != null) {
            //TODO
        }
        br.close();

        /**
         * 缓冲字符输出流
         */
        PrintWriter pw = new PrintWriter("io.txt", "utf-8");//PrintWriter可以直接创建基于文件进行写操作
        pw.close();

        PrintWriter pw1 = new PrintWriter(new OutputStreamWriter(new FileOutputStream("io.txt"), "utf-8"),
                true);//当第一个参数为流，可以使用第二个参数来指定是否自动flush
        pw1.println("......");
        pw1.close();
    }

    private void dealCsv(InputStream inputStream) {
        log.info("dealCsv start");
        CsvReader reader = null;
        try {
            //如果生产文件乱码，windows下用gbk，linux用UTF-8
            reader = new CsvReader(inputStream, ',', Charset.forName("gbk"));
            List<JSONObject> list = new ArrayList<>();
            while (reader.readRecord()) {
                String accountLogId = reader.get(0);
                if (accountLogId.contains("#")) {
                    continue;
                }
                String merchantOrderNo = reader.get(2);
                if (merchantOrderNo.startsWith("")) {
                    list.add(null);
                    if (list.size() >= 500) {
                        // TODO: 2020/5/15 处理集合数据
                        list.clear();
                    }
                }
            }
            // TODO: 2020/5/15 处理集合数据
        } catch (Exception e) {
            log.error("dealCsv error", e);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        log.info("dealCsv end");
    }

    /**
     * @param inputStream
     * @param headClass
     * @param sheetNo
     * @param headRowNumber 表头行数
     * @param batchNumber 批量处理数
     * @param dealMethod 数据处理方法
     */
    public static <T> Integer dealExcel(InputStream inputStream, Class<T> headClass, Integer sheetNo,
            Integer headRowNumber, Integer batchNumber, Consumer<List<T>> dealMethod) {
        List<T> list = new ArrayList<>();
        AtomicInteger count = new AtomicInteger();
        ExcelReaderBuilder read = EasyExcel.read(inputStream, headClass, new AnalysisEventListener<T>() {
            /**
             * 这个每一条数据解析都会来调用
             * @param e
             * @param analysisContext
             */
            @Override
            public void invoke(T e, AnalysisContext analysisContext) {
                count.incrementAndGet();
                list.add(e);
                //达到batchNumber，需要去处理一次数据，防止数据几万条数据在内存，容易OOM
                if (list.size() >= batchNumber) {
                    dealData();
                }
            }

            /**
             * 所有数据解析完成后调用
             * @param analysisContext
             */
            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                //处理剩余数据
                dealData();
            }

            /**
             * 处理数据
             */
            private void dealData() {
                List<T> data = new ArrayList<>(list);
                list.clear();
                dealMethod.accept(data);
            }
        });
        read.readCache(new MapCache()).ignoreEmptyRow(Boolean.FALSE).headRowNumber(headRowNumber).sheet(sheetNo)
                .doRead();
        return count.get();
    }
}