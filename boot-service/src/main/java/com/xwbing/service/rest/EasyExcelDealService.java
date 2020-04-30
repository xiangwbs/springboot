package com.xwbing.service.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.cache.MapCache;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.alibaba.fastjson.JSONObject;
import com.xwbing.config.redis.RedisService;
import com.xwbing.constant.ImportStatusEnum;
import com.xwbing.domain.entity.rest.ImportFailLog;
import com.xwbing.domain.entity.rest.ImportTask;
import com.xwbing.domain.entity.vo.EasyExcelHeadVo;
import com.xwbing.domain.entity.vo.ExcelProcessVo;
import com.xwbing.domain.entity.vo.ExcelVo;
import com.xwbing.exception.BusinessException;
import com.xwbing.exception.UtilException;
import com.xwbing.util.PassWordUtil;
import com.xwbing.util.RestMessage;
import com.xwbing.util.SensitiveWordEngine;

import lombok.extern.slf4j.Slf4j;

/**
 * excel 处理
 *
 * @author daofeng
 * @version $Id$
 * @since 2020年04月12日 上午12:04
 */
@Slf4j
@Service
public class EasyExcelDealService {
    private static final String EXCEL_DEAL_COUNT_PREFIX = "excel_deal_count_";
    @Resource
    private RedisService redisService;
    @Resource
    private ThreadPoolTaskExecutor taskExecutor;
    @Resource
    private ImportTaskService importTaskService;
    @Resource
    private ImportFailLogService importFailLogService;
    @Resource
    private SensitiveWordEngine sensitiveWordEngine;

    // ---------------------- 实战 ----------------------

    /**
     * 导出失败数据到浏览器
     *
     * @param response
     * @param importId
     */
    public void download(HttpServletResponse response, String importId) {
        ImportTask importTask = importTaskService.getById(importId);
        if (importTask == null) {
            throw new BusinessException("导入任务不存在");
        }
        List<List<Object>> excelData = new ArrayList<>();
        List<ImportFailLog> importFailLogs = importFailLogService.listByImportId(importId);
        importFailLogs.forEach(importFailLog -> {
            List<Object> list = new ArrayList<>();
            String content = importFailLog.getContent();
            EasyExcelHeadVo headVo = JSONObject.parseObject(content, EasyExcelHeadVo.class);
            list.add(headVo.getName());
            list.add(headVo.getAge());
            list.add(headVo.getTel());
            list.add(headVo.getIntroduction());
            list.add(importFailLog.getRemark());
            excelData.add(list);
        });
        if (CollectionUtils.isNotEmpty(excelData)) {
            try (ServletOutputStream outputStream = response.getOutputStream()) {
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/octet-stream");
                //防止中文乱码
                String fileName = URLEncoder.encode(importTask.getFileName(), "UTF-8");
                response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
                response.setHeader("Pragma", "No-cache");
                response.setHeader("Cache-Control", "no-cache");
                response.setDateHeader("Expires", 0);
                EasyExcel.write(outputStream, EasyExcelHeadVo.class).autoTrim(Boolean.TRUE).sheet("sheet0")
                        .doWrite(excelData);
            } catch (Exception e) {
                log.error("downloadExcelError with importId={}", importId, e);
                throw new BusinessException("下载文件失败");
            }
        } else {
            throw new BusinessException("无失败数据");
        }
    }

    /**
     * 读取excel
     *
     * analysisEventListener不能被spring管理，每次读取excel都要new，如果里面用到springBean可以用构造方法传进去
     * 超过5M，默认会用ehcache
     * ignoreEmptyRow默认为true 如要要统计进度条 改为false比较好
     *
     * @param excel
     * @param sheetNo
     * @param headRowNum 如果从0开始 会读取到表头数据 默认为1
     *
     * @return
     */
    public String read(MultipartFile excel, int sheetNo, int headRowNum) {
        String filename = excel.getOriginalFilename();
        if (!checkType(filename)) {
            throw new BusinessException("文件格式不正确");
        }
        ImportTask importTask = ImportTask.builder().fileName(filename).status(ImportStatusEnum.EXPORT.getCode())
                .needDownload(false).build();
        RestMessage restMessage = importTaskService.save(importTask);
        String importId = restMessage.getId();
        CompletableFuture.runAsync(() -> {
            try (InputStream inputStream = excel.getInputStream()) {
                EasyExcel.read(inputStream, ExcelVo.class,
                        new EasyExcelReadListener(importId, this, taskExecutor, importTaskService,
                                importFailLogService)).readCache(new MapCache()).ignoreEmptyRow(Boolean.FALSE)
                        .headRowNumber(headRowNum).sheet(sheetNo).doRead();
            } catch (IOException e) {
                log.error("readExcelError importId:{} error:{}", importId, ExceptionUtils.getStackTrace(e));
            }
        });
        return importId;
    }

    /**
     * 获取excel进度条
     *
     * @param importId
     *
     * @return
     */
    public ExcelProcessVo getProcess(String importId) {
        ImportTask importTask = importTaskService.getById(importId);
        if (importTask == null) {
            throw new BusinessException("导入任务不存在");
        }
        Integer totalCount = Optional.ofNullable(importTask.getTotalCount()).orElse(0);
        Integer failCount = Optional.ofNullable(importTask.getFailCount()).orElse(0);
        Integer successCount = totalCount - failCount;
        String status = importTask.getStatus();
        if (ImportStatusEnum.FAIL.getCode().equals(status)) {
            return ExcelProcessVo.builder().process(100).errorCount(failCount).successCount(successCount)
                    .msg(importTask.getDetail()).success(false).build();
        } else {
            int process;
            if (ImportStatusEnum.SUCCESS.getCode().equals(status)) {
                process = 100;
            } else if (totalCount.equals(0)) {
                process = 0;
            } else {
                String deal = redisService.get(EXCEL_DEAL_COUNT_PREFIX + importId);
                int dealCount = StringUtils.isEmpty(deal) ? 0 : Integer.valueOf(deal);
                process = new BigDecimal(dealCount).divide(new BigDecimal(totalCount), 2, BigDecimal.ROUND_HALF_UP)
                        .multiply(new BigDecimal(100)).intValue();
            }
            return ExcelProcessVo.builder().process(process).errorCount(importTask.getFailCount())
                    .successCount(successCount).msg(importTask.getDetail()).success(true).build();
        }
    }

    /**
     * 处理数据
     *
     * @param list
     * @param importId
     */
    public void dealExcelData(List<ExcelVo> list, String importId) {
        log.info("dealExcelData importId:{}", importId);
        int size = list.size();
        try {
            //数据校验
            list = list.stream().filter(excelVo -> {
                if (StringUtils.isEmpty(excelVo.getName()) || StringUtils.isEmpty(excelVo.getTel())) {
                    importFailLogService.save(importId, JSONObject.toJSONString(excelVo), "姓名和电话不能为空");
                    return false;
                }
                if (sensitiveWordEngine.isContainSensitiveWord(excelVo.getIntroduction())) {
                    importFailLogService.save(importId, JSONObject.toJSONString(excelVo), "简介不能包含敏感词汇");
                    return false;
                }
                return true;
            }).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(list)) {
                //处理正确数据
            }
        } catch (Exception e) {
            log.error("dealExcelDataError importId:{} error:{}", importId, ExceptionUtils.getStackTrace(e));
        } finally {
            redisService.incrBy(EXCEL_DEAL_COUNT_PREFIX + importId, size);
            redisService.expire(EXCEL_DEAL_COUNT_PREFIX + importId, 60 * 30);
        }
    }

    // ---------------------- 示例 ----------------------

    /**
     * 文件下载
     * 动态头
     * 默认关闭流,如果错误信息以流的形式呈现，不能关闭流 .autoCloseStream(Boolean.FALSE)
     * password为null不加密
     *
     * @param response
     * @param fileName
     * @param sheetName
     * @param password
     * @param heads
     * @param excelData
     */
    public void download(HttpServletResponse response, String fileName, String sheetName, String password,
            List<String> heads, List<List<Object>> excelData) {
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
            //自动列宽
            EasyExcel.write(outputStream).head(getHead(heads))
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()).password(password)
                    .sheet(sheetName).autoTrim(Boolean.TRUE).doWrite(excelData);
        } catch (Exception e) {
            log.error("excel download error:{}", e.getMessage());
            throw new UtilException("下载文件失败");
            // response.reset();
            // response.setContentType("application/json");
            // response.setCharacterEncoding("utf-8");
            // response.getWriter().println("下载文件失败");
        }
    }

    /**
     * 生成excel
     *
     * @param basedir
     * @param fileName
     * @param sheetName
     * @param password
     * @param excelData
     */
    public void write(String basedir, String fileName, String sheetName, String password,
            List<List<Object>> excelData) {
        Path path = FileSystems.getDefault().getPath(basedir, fileName + ExcelTypeEnum.XLSX.getValue());
        try (OutputStream out = Files.newOutputStream(path)) {
            EasyExcel.write(out).head(ExcelVo.class).registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .password(password).sheet(sheetName).autoTrim(Boolean.TRUE).doWrite(excelData);
        } catch (IOException e) {
            log.error("excel write error:{}", e.getMessage());
        }
    }

    // /**
    //  * 生成加密excel
    //  *
    //  * @param basedir
    //  * @param fileName
    //  * @param sheetName
    //  * @param password
    //  * @param heads
    //  * @param excelData
    //  */
    // public static void encryptWrite(String basedir, String fileName, String sheetName, String password,
    //         List<String> heads, List<List<Object>> excelData) throws IOException {
    //     Path path = FileSystems.getDefault().getPath(basedir, fileName + ExcelTypeEnum.XLSX.getValue());
    //     OPCPackage opc = null;
    //     try (OutputStream out = Files.newOutputStream(path);
    //             OutputStream encryptOut = Files.newOutputStream(path);
    //             POIFSFileSystem fs = new POIFSFileSystem()) {
    //         //自动列宽,自动关闭流
    //         EasyExcel.write(out).head(getHead(heads)).registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
    //                 .sheet(sheetName).doWrite(excelData);
    //         //添加密码保护
    //         EncryptionInfo info = new EncryptionInfo(EncryptionMode.agile);
    //         Encryptor enc = info.getEncryptor();
    //         enc.confirmPassword(password);
    //         //加密文件
    //         opc = OPCPackage.open(path.toFile(), PackageAccess.READ_WRITE);
    //         opc.save(enc.getDataStream(fs));
    //         fs.writeFilesystem(encryptOut);
    //     } catch (Exception e) {
    //         log.error("excel write error:{}", e.getMessage());
    //     } finally {
    //         if (opc != null) {
    //             opc.close();
    //         }
    //     }
    // }

    /**
     * @param filePath
     * @param sheetNo
     * @param headRowNum
     *
     * @return
     */
    public String read(String filePath, int sheetNo, int headRowNum) {
        String fileName = FileSystems.getDefault().getPath(filePath).toString();
        if (!checkType(fileName)) {
            throw new BusinessException("文件格式不正确");
        }
        String importId = PassWordUtil.createUuId();
        CompletableFuture.runAsync(() -> EasyExcel.read(fileName,
                new EasyExcelReadListener(importId, this, taskExecutor, importTaskService, importFailLogService))
                .head(ExcelVo.class).readCache(new MapCache()).headRowNumber(headRowNum).ignoreEmptyRow(Boolean.FALSE)
                .sheet(sheetNo).doRead());
        return importId;
    }

    /**
     * 获取表头
     *
     * @param heads
     *
     * @return
     */
    private List<List<String>> getHead(List<String> heads) {
        List<List<String>> list = new ArrayList<>();
        heads.forEach(title -> list.add(Collections.singletonList(title)));
        return list;
    }

    private boolean checkType(String fileName) {
        String type = fileName.substring(fileName.lastIndexOf("."));
        return ExcelTypeEnum.XLSX.getValue().equals(type) || ExcelTypeEnum.XLS.getValue().equals(type);
    }
}
