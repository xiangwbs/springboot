package com.xwbing.service.util;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.cache.MapCache;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.xwbing.service.exception.ExcelException;

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
    public static <T> Integer read(InputStream inputStream, Class<T> headClass, Integer sheetNo, Integer headRowNumber,
            Integer batchNumber, Consumer<List<T>> dealMethod) {
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

    public static <T> Integer readByLocal(String fullPath, Class<T> headClass, Integer sheetNo, Integer headRowNumber,
            Integer batchNumber, Consumer<List<T>> dealMethod) {
        String type = fullPath.substring(fullPath.lastIndexOf("."));
        if (!(ExcelTypeEnum.XLSX.getValue().equals(type) || ExcelTypeEnum.XLS.getValue().equals(type))) {
            throw new ExcelException("文件格式不正确");
        }
        List<T> list = new ArrayList<>();
        AtomicInteger count = new AtomicInteger();
        ExcelReaderBuilder read = EasyExcel.read(fullPath, headClass, new AnalysisEventListener<T>() {
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