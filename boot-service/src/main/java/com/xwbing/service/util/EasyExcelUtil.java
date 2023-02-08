package com.xwbing.service.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.cache.MapCache;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;

/**
 * @author daofeng
 * @version $Id$
 * @since 2023年02月08日 7:06 PM
 */
public class EasyExcelUtil {
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
        read.readCache(new MapCache()).ignoreEmptyRow(Boolean.TRUE).headRowNumber(headRowNumber).sheet(sheetNo)
                .doRead();
        return count.get();
    }
}
