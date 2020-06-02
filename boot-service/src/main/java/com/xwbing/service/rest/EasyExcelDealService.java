package com.xwbing.service.rest;

import java.io.File;
import java.io.IOException;
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
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.cache.MapCache;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
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
    private static final int EXPORT_PAGE_SIZE = 500;
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
     * cell最大长度为32767
     *
     * @param response
     * @param importId
     */
    public void writeToBrowser(HttpServletResponse response, String importId) {
        ImportTask importTask = importTaskService.getById(importId);
        if (importTask == null) {
            throw new BusinessException("导入任务不存在");
        }
        Page<Object> page = PageHelper.startPage(1, 1);
        importFailLogService.listByImportId(importId);
        long count = page.getTotal();
        if (count == 0) {
            return;
        }
        ExcelWriter excelWriter = null;
        long times = (count % EXPORT_PAGE_SIZE == 0) ? count / EXPORT_PAGE_SIZE : (count / EXPORT_PAGE_SIZE + 1);
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/octet-stream");
            //防止中文乱码
            String fileName = URLEncoder.encode(importTask.getFileName(), "UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            excelWriter = EasyExcel.write(outputStream, EasyExcelHeadVo.class).build();
            WriteSheet writeSheet = EasyExcel.writerSheet("sheet0").autoTrim(Boolean.TRUE).build();
            List<ImportFailLog> importFailLogs;
            for (int i = 1; i <= times; i++) {
                PageHelper.startPage(i, EXPORT_PAGE_SIZE);
                importFailLogs = importFailLogService.listByImportId(importId);
                List<EasyExcelHeadVo> excelData = importFailLogs.stream().map(importFailLog -> {
                    EasyExcelHeadVo headVo = JSONObject.parseObject(importFailLog.getContent(), EasyExcelHeadVo.class);
                    headVo.setRemark(importFailLog.getRemark());
                    return headVo;
                }).collect(Collectors.toList());
                excelWriter.write(excelData, writeSheet);
            }
        } catch (Exception e) {
            log.error("writeToBrowser importId={} error", importId, e);
            throw new BusinessException("下载文件失败");
        } finally {
            if (excelWriter != null) {
                excelWriter.finish();
            }
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
    public String readByStream(MultipartFile excel, int sheetNo, int headRowNum) {
        String filename = excel.getOriginalFilename();
        if (StringUtils.isEmpty(filename)) {
            throw new BusinessException("请选择文件");
        }
        String type = filename.substring(filename.lastIndexOf("."));
        if (!(ExcelTypeEnum.XLSX.getValue().equals(type) || ExcelTypeEnum.XLS.getValue().equals(type))) {
            throw new BusinessException("文件格式不正确");
        }
        ImportTask importTask = ImportTask.builder().fileName(filename).status(ImportStatusEnum.EXPORT.getCode())
                .needDownload(false).build();
        RestMessage restMessage = importTaskService.save(importTask);
        String importId = restMessage.getId();
        try {
            //将上传文件复制到自定义临时文件,提高效率。用默认临时文件，多线程高并发下会出现FileNotFoundException
            File tmpFile = File.createTempFile(filename, type);
            FileUtils.copyInputStreamToFile(excel.getInputStream(), tmpFile);
            CompletableFuture.runAsync(() -> EasyExcel.read(tmpFile, ExcelVo.class,
                    new EasyExcelReadListener(importId, tmpFile, this, taskExecutor, importTaskService,
                            importFailLogService)).readCache(new MapCache()).ignoreEmptyRow(Boolean.FALSE)
                    .headRowNumber(headRowNum).sheet(sheetNo).doRead()).exceptionally(throwable -> {
                log.error("readByStream importId:{} error", importId, throwable);
                throw new BusinessException(throwable);
            });
        } catch (Exception e) {
            ImportTask fail = ImportTask.builder().id(importId).status(ImportStatusEnum.FAIL.getCode())
                    .detail("系统异常，请重新导入").build();
            importTaskService.update(fail);
            log.error("readByStream importId:{} error", importId, e);
        }
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
                //特殊处理，100时，前端不会再请求接口问题
                process = process == 100 ? 99 : process;
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
            log.error("dealExcelData importId:{} error", importId, e);
        } finally {
            redisService.incrBy(EXCEL_DEAL_COUNT_PREFIX + importId, size);
            redisService.expire(EXCEL_DEAL_COUNT_PREFIX + importId, 60 * 30);
        }
    }

    // ---------------------- 示例 ----------------------

    /**
     * 文件下载到浏览器
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
    public void writeToBrowser(HttpServletResponse response, String fileName, String sheetName, String password,
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
            log.error("writeToBrowser error", e);
            throw new UtilException("下载文件失败");
            // response.reset();
            // response.setContentType("application/json");
            // response.setCharacterEncoding("utf-8");
            // response.getWriter().println("下载文件失败");
        }
    }

    /**
     * 生成excel到本地
     *
     * @param basedir
     * @param fileName
     * @param sheetName
     * @param password
     * @param excelData
     */
    public void writeToLocal(String basedir, String fileName, String sheetName, String password,
            List<ExcelVo> excelData) {
        Path path = FileSystems.getDefault().getPath(basedir, fileName + ExcelTypeEnum.XLSX.getValue());
        try (OutputStream out = Files.newOutputStream(path)) {
            EasyExcel.write(out).head(ExcelVo.class).registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .password(password).sheet(sheetName).autoTrim(Boolean.TRUE).doWrite(excelData);
        } catch (IOException e) {
            log.error("writeToLocal error", e);
        }
    }

    /**
     * 生成多个sheet的excel到本地
     *
     * @param basedir
     * @param fileName
     */
    public void repeatedWriteToLocal(String basedir, String fileName) {
        Path path = FileSystems.getDefault().getPath(basedir, fileName + ExcelTypeEnum.XLSX.getValue());
        try (OutputStream out = Files.newOutputStream(path)) {
            ExcelWriter excelWriter = EasyExcel.write(out).build();
            WriteSheet writeSheet;
            for (int i = 0; i < 2; i++) {
                writeSheet = EasyExcel.writerSheet(i, "sheet" + i)
                        .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()).head(ExcelVo.class).build();
                ExcelVo java = ExcelVo.builder().name("java").age(18).tel("1348888888" + i).introduction("这是sheet" + i)
                        .build();
                excelWriter.write(Collections.singletonList(java), writeSheet);
            }
            excelWriter.finish();
        } catch (IOException e) {
            log.error("repeatedWriteToLocal error", e);
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
    public String readByLocal(String filePath, int sheetNo, int headRowNum) {
        String pathName = FileSystems.getDefault().getPath(filePath).toString();
        if (!checkType(pathName)) {
            throw new BusinessException("文件格式不正确");
        }
        String importId = PassWordUtil.createUuId();
        CompletableFuture.runAsync(() -> EasyExcel.read(pathName,
                new EasyExcelReadListener(importId, null, this, taskExecutor, importTaskService, importFailLogService))
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
