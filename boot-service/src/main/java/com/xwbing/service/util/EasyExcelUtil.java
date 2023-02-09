package com.xwbing.service.util;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.cache.MapCache;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.metadata.holder.ReadSheetHolder;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author daofeng
 * @version $Id$
 * @since 2023年02月08日 7:06 PM
 */
@Slf4j
public class EasyExcelUtil {
    /**
     * @param inputStream
     * @param fullPath
     * @param headClass
     * @param sheetNo 0-n
     * @param headRowNum 表头行数
     * @param sampleNum 示例数据行数
     * @param batchNum 批量处理数量
     * @param consumer 数据消费逻辑
     */
    public static <T> Integer read(InputStream inputStream, String fullPath, Class<T> headClass, Integer sheetNo,
            Integer headRowNum, Integer sampleNum, Integer batchNum, Consumer<List<T>> consumer) {
        AtomicInteger totalCount = new AtomicInteger();
        AnalysisEventListener<T> eventListener = new AnalysisEventListener<T>() {
            private List<T> list = new ArrayList<>();

            /**
             * 这个每一条数据解析都会来调用
             */
            @Override
            public void invoke(T e, AnalysisContext context) {
                Integer currentRowNum = context.readRowHolder().getRowIndex();
                log.info("readExcel invoke rowNum:{} data:{}", currentRowNum, JSON.toJSONString(e));
                //不处理示例数据
                if (currentRowNum < sampleNum) {
                    return;
                }
                list.add(e);
                //达到batchNumber，需要去处理一次数据，防止数据几万条数据在内存，容易OOM
                if (list.size() >= batchNum) {
                    dealData();
                }
            }

            @Override
            public void onException(Exception exception, AnalysisContext context) {
                log.error("readExcel onException error", exception);
            }

            /**
             * 所有数据解析完成后调用
             */
            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                //处理剩余数据
                dealData();
            }

            /**
             * 表头数据处理
             * headMap.get(i)
             *
             * @param headMap
             * @param context
             */
            @Override
            public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
                log.info("readExcel head:{}", JSONObject.toJSONString(headMap));
                //获取总条数
                ReadSheetHolder readSheetHolder = context.readSheetHolder();
                Integer totalRowNumber = readSheetHolder.getApproximateTotalRowNumber();
                totalRowNumber = totalRowNumber <= sampleNum ? 0 : totalRowNumber - sampleNum;
                totalCount.set(totalRowNumber);
                log.info("readExcel totalCount:{}", totalCount.intValue());
            }

            /**
             * 处理数据
             */
            private void dealData() {
                List<T> data = new ArrayList<>(list);
                list.clear();
                consumer.accept(data);
            }
        };
        ExcelReaderBuilder read;
        if (inputStream != null) {
            read = EasyExcel.read(inputStream, headClass, eventListener);
        } else if (StringUtils.isNotEmpty(fullPath)) {
            read = EasyExcel.read(fullPath, headClass, eventListener);
        } else {
            throw new RuntimeException("excel不能为空");
        }
        read.readCache(new MapCache()).ignoreEmptyRow(Boolean.TRUE).headRowNumber(headRowNum).sheet(sheetNo).doRead();
        return totalCount.get();
    }

    /**
     * 文件下载到浏览器
     * 动态头
     * 自动列宽
     * 默认关闭流,如果错误信息以流的形式呈现，不能关闭流 .autoCloseStream(Boolean.FALSE)
     * password为null不加密
     * cell最大长度为32767
     * 数据量大时，可能会oom，建议分页查询，写入到本地，再上传到oss
     *
     * @param response
     * @param fileName
     * @param sheetName
     * @param password
     * @param heads
     * @param excelData
     */
    public static void writeToBrowser(HttpServletResponse response, String fileName, String sheetName, String password,
            List<String> heads, List<List<Object>> excelData) {
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            response.setCharacterEncoding("UTF-8");
            // response.setContentType("application/vnd.ms-excel;charset=utf-8");
            response.setContentType("application/octet-stream");
            //防止中文乱码
            fileName = URLEncoder.encode(fileName, "UTF-8");
            response.setHeader("Content-Disposition",
                    "attachment; filename=" + fileName + ExcelTypeEnum.XLSX.getValue());
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            // 获取表头
            List<List<String>> head = heads.stream().map(Collections::singletonList).collect(Collectors.toList());
            // 写数据
            EasyExcel.write(outputStream).head(head).registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .password(password).sheet(sheetName).autoTrim(Boolean.TRUE).doWrite(excelData);
        } catch (Exception e) {
            throw new RuntimeException("下载文件失败");
            // response.reset();
            // response.setContentType("application/json");
            // response.setCharacterEncoding("utf-8");
            // response.getWriter().println("下载文件失败");
        }
    }

    /**
     * @param response
     * @param headClass 表头类
     * @param fileName 文件名
     * @param sheetName
     * @param password 密码
     * @param dataFunction pageNumber -> {分页和数据组装逻辑}
     */
    public static <T> void writeToBrowserByPage(HttpServletResponse response, Class<T> headClass, String fileName,
            String sheetName, String password, Function<Integer, List<T>> dataFunction) {
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/octet-stream");
            //防止中文乱码
            fileName = URLEncoder.encode(fileName, "UTF-8");
            response.setHeader("Content-Disposition",
                    "attachment; filename=" + fileName + ExcelTypeEnum.XLSX.getValue());
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);

            ExcelWriter excelWriter = EasyExcel.write(outputStream, headClass)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()).password(password).build();
            WriteSheet writeSheet = EasyExcel.writerSheet(sheetName).autoTrim(Boolean.TRUE).build();

            int pageNumber = 1;
            while (true) {
                List<T> data = dataFunction.apply(pageNumber);
                if (data.isEmpty()) {
                    break;
                }
                excelWriter.write(data, writeSheet);
                pageNumber++;
            }
            excelWriter.finish();
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    public static <T> void writeToLocal(Class<T> headClass, String basedir, String fileName, String sheetName,
            String password, List<T> excelData) {
        Path path = FileSystems.getDefault().getPath(basedir, fileName + ExcelTypeEnum.XLSX.getValue());
        EasyExcel.write(path.toString()).head(headClass)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()).password(password).sheet(sheetName)
                .autoTrim(Boolean.TRUE).doWrite(excelData);
    }

    /**
     * @param headClass
     * @param basedir
     * @param fileName
     * @param sheetName
     * @param password
     * @param dataFunction pageNumber -> {分页和数据组装逻辑}
     * @param <T>
     */
    public static <T> void writeToLocalByPage(Class<T> headClass, String basedir, String fileName, String sheetName,
            String password, Function<Integer, List<T>> dataFunction) {
        Path path = FileSystems.getDefault().getPath(basedir, fileName + ExcelTypeEnum.XLSX.getValue());
        ExcelWriter excelWriter = null;
        try {
            excelWriter = EasyExcel.write(path.toString()).head(headClass)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()).password(password).build();
            WriteSheet writeSheet = EasyExcel.writerSheet(sheetName).autoTrim(Boolean.TRUE).build();
            int pageNumber = 1;
            while (true) {
                List<T> data = dataFunction.apply(pageNumber);
                if (data.isEmpty()) {
                    break;
                }
                excelWriter.write(data, writeSheet);
                pageNumber++;
            }
        } finally {
            if (excelWriter != null) {
                excelWriter.finish();
            }
        }
    }
}