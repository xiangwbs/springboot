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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.cache.MapCache;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.metadata.holder.ReadRowHolder;
import com.alibaba.excel.read.metadata.holder.ReadSheetHolder;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author daofeng
 * @version $Id$
 * @since 2023年02月08日 7:06 PM
 */
@Slf4j
public class ExcelUtil {

    /**
     * 读取excel
     *
     * @param inputStream 文件流 2选1
     * @param fullPath 带后缀全路径 2选1
     * @param head 表头 {@link ExcelProperty}
     * @param sheetNo start form 0
     * @param headRowNum 表头行数
     * @param exampleNum 示例数据行数
     * @param batchDealNum 批处理数量(分配处理 防止oom)
     * @param headConsumer 表头消费逻辑
     * @param dataConsumer 数据消费逻辑
     * @param errorConsumer 异常消费逻辑
     */
    public static <T> Integer read(InputStream inputStream, String fullPath, Class<T> head, int sheetNo, int headRowNum,
            int exampleNum, int batchDealNum, Consumer<Map<Integer, String>> headConsumer,
            Consumer<List<T>> dataConsumer, Consumer<Error<T>> errorConsumer) {
        AtomicInteger totalCount = new AtomicInteger();
        AnalysisEventListener<T> readListener = new AnalysisEventListener<T>() {
            private List<T> list = new ArrayList<>();

            /**
             * 非表头数据处理
             */
            @Override
            public void invoke(T data, AnalysisContext context) {
                // start form 0
                Integer rowIndex = context.readRowHolder().getRowIndex();
                log.info("readExcel invoke rowIndex:{} data:{}", rowIndex, JSON.toJSONString(data));
                // 不处理示例数据
                if (rowIndex < exampleNum + headRowNum) {
                    return;
                }
                list.add(data);
                // 达到批处理数量，需要处理一次数据，防止数据几万条数据在内存，容易oom
                if (list.size() >= batchDealNum) {
                    dealData();
                }
            }

            /**
             * 异常处理
             */
            @Override
            public void onException(Exception exception, AnalysisContext context) {
                ReadSheetHolder readSheetHolder = context.readSheetHolder();
                Integer rowIndex = readSheetHolder.getRowIndex();
                ReadRowHolder readRowHolder = context.readRowHolder();
                Object data = readRowHolder.getCurrentRowAnalysisResult();
                log.error("readExcel onException rowIndex:{} data:{} error:{}", rowIndex, JSONUtil.toJsonStr(data),
                        exception.getMessage());
                Error<T> error = Error.<T>builder().rowIndex(rowIndex)
                        .data(JSONUtil.toBean(JSONUtil.toJsonStr(data), head)).exception(exception).build();
                // 自定义异常处理逻辑
                errorConsumer.accept(error);
            }

            /**
             * 所有数据解析完成后调用
             */
            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
                // 处理剩余数据
                dealData();
            }

            /**
             * 表头数据处理
             *
             * @param headMap key start form 0
             */
            @Override
            public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
                log.info("readExcel head:{}", JSONObject.toJSONString(headMap));
                if (totalCount.get() != 0) {
                    return;
                }
                // 获取总条数
                ReadSheetHolder readSheetHolder = context.readSheetHolder();
                Integer totalRowNumber = readSheetHolder.getApproximateTotalRowNumber() - headRowNum;
                totalRowNumber = totalRowNumber <= exampleNum ? 0 : totalRowNumber - exampleNum;
                totalCount.set(totalRowNumber);
                log.info("readExcel totalCount:{}", totalCount.intValue());
                // 自定义表头处理逻辑 可处理校验表头之类的逻辑
                headConsumer.accept(headMap);
            }

            /**
             * 批量处理数据
             */
            private void dealData() {
                List<T> data = new ArrayList<>(list);
                list.clear();
                // 自定义数据处理逻辑
                dataConsumer.accept(data);
            }
        };
        ExcelReaderBuilder read;
        if (inputStream != null) {
            read = EasyExcel.read(inputStream, head, readListener);
        } else if (StringUtils.isNotEmpty(fullPath)) {
            read = EasyExcel.read(fullPath, head, readListener);
        } else {
            throw new RuntimeException("excel不能为空");
        }
        read.readCache(new MapCache()).ignoreEmptyRow(Boolean.TRUE).headRowNumber(headRowNum).sheet(sheetNo).doRead();
        return totalCount.get();
    }

    /**
     * 文件下载到浏览器
     *
     * @param response
     * @param fileName 不带文件后缀
     * @param password 为null不加密
     * @param heads 动态表头数据
     * @param excelData excel数据 数据量大时，可能会oom，建议分页查询，写入到本地，再上传到oss
     */
    public static void writeToBrowser(HttpServletResponse response, String fileName, String password,
            List<String> heads, List<List<Object>> excelData) {
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/octet-stream");
            // 防止中文乱码
            fileName = URLEncoder.encode(fileName, "UTF-8");
            response.setHeader("Content-Disposition",
                    "attachment; filename=" + fileName + ExcelTypeEnum.XLSX.getValue());
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            // 获取动态表头
            List<List<String>> head = heads.stream().map(Collections::singletonList).collect(Collectors.toList());
            // 写数据
            EasyExcel.write(outputStream).head(head).registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .password(password).sheet("Sheet1").autoTrim(Boolean.TRUE).doWrite(excelData);
        } catch (Exception e) {
            log.error("writeToBrowser error", e);
            throw new RuntimeException("文件下载失败");
        }
    }

    /**
     * 文件下载到浏览器
     *
     * @param response
     * @param head 表头 {@link ExcelProperty}
     * @param fileName 不带文件后缀
     * @param password 为null不加密
     * @param dataFunction pageNum -> {分页数据组装逻辑} start form 1
     */
    public static <T> void writeToBrowser(HttpServletResponse response, Class<T> head, String fileName, String password,
            Function<Integer, List<T>> dataFunction) {
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/octet-stream");
            // 防止中文乱码
            fileName = URLEncoder.encode(fileName, "UTF-8");
            response.setHeader("Content-Disposition",
                    "attachment; filename=" + fileName + ExcelTypeEnum.XLSX.getValue());
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);

            ExcelWriter excelWriter = EasyExcel.write(outputStream, head)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()).password(password).build();
            WriteSheet writeSheet = EasyExcel.writerSheet("Sheet1").autoTrim(Boolean.TRUE).build();

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
            log.error("writeToBrowser error", e);
            throw new RuntimeException("文件下载失败");
        }
    }

    /**
     * 文件下载到本地
     *
     * @param head 表头 {@link ExcelProperty}
     * @param basedir 文件夹路径
     * @param fileName 不带文件后缀
     * @param password 为null不加密
     * @param excelData excel数据
     * @param dataFunction pageNum -> {分页数据组装逻辑} start form 1
     * @param <T>
     */
    public static <T> void writeToLocal(Class<T> head, String basedir, String fileName, String password,
            List<T> excelData, Function<Integer, List<T>> dataFunction) {
        Path path = FileSystems.getDefault().getPath(basedir, fileName + ExcelTypeEnum.XLSX.getValue());
        if (CollectionUtils.isNotEmpty(excelData)) {
            EasyExcel.write(path.toString()).head(head).registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .password(password).sheet("Sheet1").autoTrim(Boolean.TRUE).doWrite(excelData);
        } else if (dataFunction != null) {
            ExcelWriter excelWriter = EasyExcel.write(path.toString()).head(head)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()).password(password).build();
            WriteSheet writeSheet = EasyExcel.writerSheet("Sheet1").autoTrim(Boolean.TRUE).build();
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
        } else {
            throw new RuntimeException("生成excel数据不能为空");
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Error<T> {
        private Integer rowIndex;
        private T data;
        private Exception exception;
    }
}