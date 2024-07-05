package com.xwbing.service.util.excel;

import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.cache.MapCache;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.read.builder.ExcelReaderBuilder;
import com.alibaba.excel.read.metadata.holder.ReadRowHolder;
import com.alibaba.excel.read.metadata.holder.ReadSheetHolder;
import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.builder.ExcelWriterSheetBuilder;
import com.alibaba.excel.write.handler.WriteHandler;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author daofeng
 * @version $Id$
 * @since 2023年02月08日 7:06 PM
 */
@Slf4j
public class ExcelUtil {
    public static Integer read(InputStream inputStream, int sheetNo, int headRowNum, int batchDealNum, Consumer<Map<Integer, String>> headConsumer, Consumer<List<Map<Integer, String>>> dataConsumer) {
        AtomicInteger totalCount = new AtomicInteger();
        AnalysisEventListener<Map<Integer, String>> readListener = new AnalysisEventListener<Map<Integer, String>>() {
            private final List<Map<Integer, String>> list = new ArrayList<>();

            /**
             * 异常处理
             */
            @Override
            public void onException(Exception exception, AnalysisContext context) {
                ReadSheetHolder readSheetHolder = context.readSheetHolder();
                Integer rowIndex = readSheetHolder.getRowIndex();
                ReadRowHolder readRowHolder = context.readRowHolder();
                Object data = readRowHolder.getCurrentRowAnalysisResult();
                log.error("readExcel onException rowIndex:{} data:{} error:{}", rowIndex, JSONUtil.toJsonStr(data), exception.getMessage());
            }

            /**
             * 表头数据处理
             *
             * @param headMap key start form 0
             */
            @Override
            public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
                log.info("readExcel head:{}", JSONObject.toJSONString(headMap));
                if (totalCount.get() == 0) {
                    // 获取总条数
                    ReadSheetHolder readSheetHolder = context.readSheetHolder();
                    int totalRowNumber = readSheetHolder.getApproximateTotalRowNumber() - headRowNum;
                    totalCount.set(totalRowNumber);
                    log.info("readExcel totalCount:{}", totalCount.intValue());
                }
                // 自定义表头处理逻辑
                if (headConsumer != null) {
                    headConsumer.accept(headMap);
                }
            }

            /**
             * 非表头数据处理
             */
            @Override
            public void invoke(Map<Integer, String> data, AnalysisContext context) {
                // start form 0
                Integer rowIndex = context.readRowHolder().getRowIndex();
                log.info("readExcel invoke rowIndex:{} data:{}", rowIndex, JSON.toJSONString(data));
                list.add(data);
                // 达到批处理数量，需要处理一次数据，防止数据几万条数据在内存，容易oom
                if (list.size() >= batchDealNum) {
                    dealData();
                }
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
             * 批量处理数据
             */
            private void dealData() {
                List<Map<Integer, String>> data = new ArrayList<>(list);
                list.clear();
                // 自定义数据处理逻辑
                if (dataConsumer != null) {
                    dataConsumer.accept(data);
                }
            }
        };
        EasyExcel.read(inputStream, readListener).readCache(new MapCache()).ignoreEmptyRow(Boolean.FALSE).headRowNumber(headRowNum).sheet(sheetNo).doRead();
        return totalCount.get();
    }

    /**
     * @param inputStream  文件流
     * @param head         表头 {@link ExcelProperty}
     * @param sheetNo      start form 0
     * @param batchDealNum 批处理数量(分批处理 防止oom)
     * @param dataConsumer 数据消费 数据存储等处理逻辑
     */
    public static <T> Integer read(InputStream inputStream, Class<T> head, int sheetNo, int batchDealNum, Consumer<List<T>> dataConsumer) {
        return read(inputStream, null, null, head, sheetNo, 1, 0, batchDealNum, null, dataConsumer, null);
    }

    /**
     * @param fullPath     带后缀全路径
     * @param head         表头 {@link ExcelProperty}
     * @param sheetNo      start form 0
     * @param batchDealNum 批处理数量(分批处理 防止oom)
     * @param dataConsumer 数据消费 数据存储等处理逻辑
     */
    public static <T> Integer read(String fullPath, Class<T> head, int sheetNo, int batchDealNum, Consumer<List<T>> dataConsumer) {
        return read(null, fullPath, null, head, sheetNo, 1, 0, batchDealNum, null, dataConsumer, null);
    }

    /**
     * @param inputStream   文件流
     * @param password      为null无密码
     * @param head          表头 {@link ExcelProperty}
     * @param sheetNo       start form 0
     * @param headRowNum    表头行数
     * @param exampleNum    示例数据行数
     * @param batchDealNum  批处理数量(分批处理 防止oom)
     * @param headConsumer  表头消费 校验表头是否正确等处理逻辑
     * @param dataConsumer  数据消费 数据存储等处理逻辑
     * @param errorConsumer 异常消费 读取数据异常处理逻辑
     */
    public static <T> Integer read(InputStream inputStream, String password, Class<T> head, int sheetNo, int headRowNum,
                                   int exampleNum, int batchDealNum, Consumer<Map<Integer, String>> headConsumer,
                                   Consumer<List<T>> dataConsumer, Consumer<ReadError<T>> errorConsumer) {
        return read(inputStream, null, password, head, sheetNo, headRowNum, exampleNum, batchDealNum, headConsumer, dataConsumer, errorConsumer);
    }

    /**
     * @param fullPath      带后缀全路径
     * @param password      为null无密码
     * @param head          表头 {@link ExcelProperty}
     * @param sheetNo       start form 0
     * @param headRowNum    表头行数
     * @param exampleNum    示例数据行数
     * @param batchDealNum  批处理数量(分批处理 防止oom)
     * @param headConsumer  表头消费 校验表头是否正确等处理逻辑
     * @param dataConsumer  数据消费 数据存储等处理逻辑
     * @param errorConsumer 异常消费 读取数据异常处理逻辑
     */
    public static <T> Integer read(String fullPath, String password, Class<T> head, int sheetNo, int headRowNum,
                                   int exampleNum, int batchDealNum, Consumer<Map<Integer, String>> headConsumer,
                                   Consumer<List<T>> dataConsumer, Consumer<ReadError<T>> errorConsumer) {
        return read(null, fullPath, password, head, sheetNo, headRowNum, exampleNum, batchDealNum, headConsumer, dataConsumer, errorConsumer);
    }

    /**
     * @param response
     * @param head     表头 {@link ExcelProperty}
     * @param fileName xxx.xlsx
     * @param password 为null不加密
     * @param allData  excel全量数据 数据量大时 可能会oom 建议分页查询
     */
    public static <T> void write(WriteHandler writeHandler, HttpServletResponse response, Class<T> head, String fileName, String password, List<T> allData) {
        write(writeHandler, response, null, head, fileName, password, allData, null);
    }

    public static void write(WriteHandler writeHandler, HttpServletResponse response, List<List<String>> head, String fileName, String password, List<?> allData) {
        write(writeHandler, response, null, head, fileName, password, allData, null);
    }


    /**
     * @param basedir  文件夹路径
     * @param head     表头 {@link ExcelProperty}
     * @param fileName xxx.xlsx
     * @param password 为null不加密
     * @param allData  excel全量数据 数据量大时 可能会oom 建议分页查询
     */
    public static <T> void write(WriteHandler writeHandler, String basedir, Class<T> head, String fileName, String password, List<T> allData) {
        write(writeHandler, null, basedir, head, fileName, password, allData, null);
    }

    public static void write(WriteHandler writeHandler, String basedir, List<List<String>> head, String fileName, String password, List<?> allData) {
        write(writeHandler, null, basedir, head, fileName, password, allData, null);
    }

    /**
     * @param response     * @param head 表头 {@link ExcelProperty}
     * @param fileName     xxx.xlsx
     * @param password     为null不加密
     * @param pageFunction 分页数据组装逻辑 pageNo start form 1
     */
    public static <T> void write(WriteHandler writeHandler, HttpServletResponse response, Class<T> head, String fileName, String password, Function<Integer, List<T>> pageFunction) {
        write(writeHandler, response, null, head, fileName, password, null, pageFunction);
    }

    public static void write(WriteHandler writeHandler, HttpServletResponse response, List<List<String>> head, String fileName, String password, Function<Integer, List<?>> pageFunction) {
        write(writeHandler, response, null, head, fileName, password, null, pageFunction);
    }

    /**
     * @param basedir      文件夹路径
     * @param head         表头 {@link ExcelProperty}
     * @param fileName     xxx.xlsx
     * @param password     为null不加密
     * @param pageFunction 分页数据组装逻辑 pageNo start form 1
     */
    public static <T> void write(WriteHandler writeHandler, String basedir, Class<T> head, String fileName, String password, Function<Integer, List<T>> pageFunction) {
        write(writeHandler, null, basedir, head, fileName, password, null, pageFunction);
    }

    public static void write(WriteHandler writeHandler, String basedir, List<List<String>> head, String fileName, String password, Function<Integer, List<?>> pageFunction) {
        write(writeHandler, null, basedir, head, fileName, password, null, pageFunction);
    }

    /**
     * @param inputStream   2选1 文件流
     * @param fullPath      2选1 带后缀全路径
     * @param password      为null无密码
     * @param head          表头 {@link ExcelProperty}
     * @param sheetNo       start form 0
     * @param headRowNum    表头行数
     * @param exampleNum    示例数据行数
     * @param batchDealNum  批处理数量(分批处理 防止oom)
     * @param headConsumer  表头消费 校验表头是否正确等处理逻辑
     * @param dataConsumer  数据消费 数据存储等处理逻辑
     * @param errorConsumer 异常消费 读取数据异常处理逻辑
     */
    private static <T> Integer read(InputStream inputStream, String fullPath, String password, Class<T> head, int sheetNo,
                                    int headRowNum, int exampleNum, int batchDealNum, Consumer<Map<Integer, String>> headConsumer,
                                    Consumer<List<T>> dataConsumer, Consumer<ReadError<T>> errorConsumer) {
        AtomicInteger totalCount = new AtomicInteger();
        AnalysisEventListener<T> readListener = new AnalysisEventListener<T>() {
            private final List<T> list = new ArrayList<>();

            @Override
            public void onException(Exception exception, AnalysisContext context) {
                ReadSheetHolder readSheetHolder = context.readSheetHolder();
                Integer rowIndex = readSheetHolder.getRowIndex();
                ReadRowHolder readRowHolder = context.readRowHolder();
                Object data = readRowHolder.getCurrentRowAnalysisResult();
                log.error("readExcel onException rowIndex:{} data:{} error:{}", rowIndex, JSONUtil.toJsonStr(data), exception.getMessage());
                ReadError<T> error = ReadError.<T>builder().rowIndex(rowIndex).data(JSONUtil.toBean(JSONUtil.toJsonStr(data), head)).exception(exception).build();
                // 自定义异常处理逻辑
                if (errorConsumer != null) {
                    errorConsumer.accept(error);
                }
            }

            /**
             * 表头数据处理
             *
             * @param headMap key start form 0
             */
            @Override
            public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
                log.info("readExcel head:{}", JSONObject.toJSONString(headMap));
                if (totalCount.get() == 0) {
                    // 获取总条数
                    ReadSheetHolder readSheetHolder = context.readSheetHolder();
                    int totalRowNumber = readSheetHolder.getApproximateTotalRowNumber() - headRowNum;
                    totalRowNumber = totalRowNumber <= exampleNum ? 0 : totalRowNumber - exampleNum;
                    totalCount.set(totalRowNumber);
                }
                log.info("readExcel totalCount:{}", totalCount.intValue());
                // 自定义表头处理逻辑
                if (headConsumer != null) {
                    headConsumer.accept(headMap);
                }
            }

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
             * 所有数据解析完成后调用
             */
            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
                // 处理剩余数据
                dealData();
            }

            /**
             * 批量处理数据
             */
            private void dealData() {
                List<T> data = new ArrayList<>(list);
                list.clear();
                // 自定义数据处理逻辑
                if (dataConsumer != null) {
                    dataConsumer.accept(data);
                }
            }
        };
        ExcelReaderBuilder read;
        if (StringUtils.isNotEmpty(fullPath)) {
            read = EasyExcel.read(fullPath, head, readListener);
        } else if (inputStream != null) {
            read = EasyExcel.read(inputStream, head, readListener);
        } else {
            throw new RuntimeException("excel不能为空");
        }
        read.readCache(new MapCache()).password(password).ignoreEmptyRow(Boolean.FALSE).headRowNumber(headRowNum).sheet(sheetNo).doRead();
        return totalCount.get();
    }

    /**
     * @param response     2选1
     * @param basedir      2选1 文件夹路径
     * @param head         表头 {@link ExcelProperty}
     * @param fileName     xxx.xlsx
     * @param password     为null不加密
     * @param allData      2选1 excel全量数据 数据量大时 可能会oom 建议分页查询
     * @param pageFunction 2选1 分页数据组装逻辑 pageNo start form 1
     */
    private static <T> void write(WriteHandler writeHandler, HttpServletResponse response, String basedir, Class<T> head, String fileName, String password, List<T> allData, Function<Integer, List<T>> pageFunction) {
        if (StringUtils.isNotEmpty(basedir)) {
            writeToLocal(writeHandler, basedir, head, fileName, password, allData, pageFunction);
        } else if (response != null) {
            writeToBrowser(writeHandler, response, head, fileName, password, allData, pageFunction);
        } else {
            throw new RuntimeException("excel不能为空");
        }
    }

    private static void write(WriteHandler writeHandler, HttpServletResponse response, String basedir, List<List<String>> head, String fileName, String password, List<?> allData, Function<Integer, List<?>> pageFunction) {
        if (StringUtils.isNotEmpty(basedir)) {
            writeToLocal(writeHandler, basedir, head, fileName, password, allData, pageFunction);
        } else if (response != null) {
            writeToBrowser(writeHandler, response, head, fileName, password, allData, pageFunction);
        } else {
            throw new RuntimeException("excel不能为空");
        }
    }

    /**
     * @param response
     * @param head         表头 {@link ExcelProperty}
     *                     动态表头     List<String> heads;heads.stream().map(Collections::singletonList).collect(Collectors.toList());
     * @param fileName     xxx.xlsx
     * @param password     为null不加密
     * @param allData      2选1 excel全量数据 数据量大时 可能会oom 建议分页查询
     *                     动态数据  List<List<Object>> excelData
     * @param pageFunction 2选1 分页数据组装逻辑 pageNo start form 1
     */
    private static <T> void writeToBrowser(WriteHandler writeHandler, HttpServletResponse response, Class<T> head, String fileName, String password, List<T> allData, Function<Integer, List<T>> pageFunction) {
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            // 防止中文乱码
            fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            if (pageFunction != null) {
                ExcelWriterBuilder writerBuilder = EasyExcel.write(outputStream).head(head).password(password);
                if (writeHandler != null) {
                    writerBuilder.registerWriteHandler(writeHandler);
                }
                ExcelWriter excelWriter = writerBuilder.build();
                WriteSheet writeSheet = EasyExcel.writerSheet("Sheet1").autoTrim(Boolean.TRUE).build();
                int pageNumber = 1;
                while (true) {
                    List<T> data = pageFunction.apply(pageNumber);
                    excelWriter.write(data, writeSheet);
                    if (CollectionUtils.isEmpty(data)) {
                        break;
                    }
                    pageNumber++;
                }
                excelWriter.finish();
            } else {
                ExcelWriterSheetBuilder writerSheetBuilder = EasyExcel.write(outputStream).head(head).password(password).sheet("Sheet1").autoTrim(Boolean.TRUE);
                if (writeHandler != null) {
                    writerSheetBuilder.registerWriteHandler(writeHandler);
                }
                writerSheetBuilder.doWrite(allData);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeToBrowser(WriteHandler writeHandler, HttpServletResponse response, List<List<String>> head, String fileName, String password, List<?> allData, Function<Integer, List<?>> pageFunction) {
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/vnd.ms-excel;charset=utf-8");
            // 防止中文乱码
            fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            if (pageFunction != null) {
                ExcelWriterBuilder writerBuilder = EasyExcel.write(outputStream).head(head).password(password);
                if (writeHandler != null) {
                    writerBuilder.registerWriteHandler(writeHandler);
                }
                ExcelWriter excelWriter = writerBuilder.build();
                WriteSheet writeSheet = EasyExcel.writerSheet("Sheet1").autoTrim(Boolean.TRUE).build();
                int pageNumber = 1;
                while (true) {
                    List<?> data = pageFunction.apply(pageNumber);
                    excelWriter.write(data, writeSheet);
                    if (CollectionUtils.isEmpty(data)) {
                        break;
                    }
                    pageNumber++;
                }
                excelWriter.finish();
            } else {
                ExcelWriterSheetBuilder writerSheetBuilder = EasyExcel.write(outputStream).head(head).password(password).sheet("Sheet1").autoTrim(Boolean.TRUE);
                if (writeHandler != null) {
                    writerSheetBuilder.registerWriteHandler(writeHandler);
                }
                writerSheetBuilder.doWrite(allData);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param basedir      文件夹路径
     * @param head         表头 {@link ExcelProperty}
     * @param fileName     xxx.xlsx
     * @param password     为null不加密
     * @param allData      2选1 excel全量数据 数据量大时 可能会oom 建议分页查询
     * @param pageFunction 2选1 分页数据组装逻辑 pageNo start form 1
     */
    private static <T> void writeToLocal(WriteHandler writeHandler, String basedir, Class<T> head, String fileName, String password, List<T> allData, Function<Integer, List<T>> pageFunction) {
        Path path = FileSystems.getDefault().getPath(basedir, fileName);
        if (pageFunction != null) {
            ExcelWriterBuilder writerBuilder = EasyExcel.write(path.toString()).head(head).password(password);
            if (writeHandler != null) {
                writerBuilder.registerWriteHandler(writeHandler);
            }
            ExcelWriter excelWriter = writerBuilder.build();
            WriteSheet writeSheet = EasyExcel.writerSheet("Sheet1").autoTrim(Boolean.TRUE).build();
            int pageNumber = 1;
            while (true) {
                List<T> data = pageFunction.apply(pageNumber);
                excelWriter.write(data, writeSheet);
                if (CollectionUtils.isEmpty(data)) {
                    break;
                }
                pageNumber++;
            }
            excelWriter.finish();
        } else {
            ExcelWriterSheetBuilder writerSheetBuilder = EasyExcel.write(path.toString()).head(head).password(password).sheet("Sheet1").autoTrim(Boolean.TRUE);
            if (writeHandler != null) {
                writerSheetBuilder.registerWriteHandler(writeHandler);
            }
            writerSheetBuilder.doWrite(allData);
        }
    }

    private static void writeToLocal(WriteHandler writeHandler, String basedir, List<List<String>> head, String fileName, String password, List<?> allData, Function<Integer, List<?>> pageFunction) {
        Path path = FileSystems.getDefault().getPath(basedir, fileName);
        if (pageFunction != null) {
            ExcelWriterBuilder writerBuilder = EasyExcel.write(path.toString()).head(head).password(password);
            if (writeHandler != null) {
                writerBuilder.registerWriteHandler(writeHandler);
            }
            ExcelWriter excelWriter = writerBuilder.build();
            WriteSheet writeSheet = EasyExcel.writerSheet("Sheet1").autoTrim(Boolean.TRUE).build();
            int pageNumber = 1;
            while (true) {
                List<?> data = pageFunction.apply(pageNumber);
                excelWriter.write(data, writeSheet);
                if (CollectionUtils.isEmpty(data)) {
                    break;
                }
                pageNumber++;
            }
            excelWriter.finish();
        } else {
            ExcelWriterSheetBuilder writerSheetBuilder = EasyExcel.write(path.toString()).head(head).password(password).sheet("Sheet1").autoTrim(Boolean.TRUE);
            if (writeHandler != null) {
                writerSheetBuilder.registerWriteHandler(writeHandler);
            }
            writerSheetBuilder.doWrite(allData);
        }
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReadError<T> {
        private Integer rowIndex;
        private T data;
        private Exception exception;
    }
}