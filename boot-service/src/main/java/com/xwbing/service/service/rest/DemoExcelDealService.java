package com.xwbing.service.service.rest;

import java.io.File;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.cache.MapCache;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.xwbing.service.domain.entity.rest.ImportFailLog;
import com.xwbing.service.domain.entity.rest.ImportTask;
import com.xwbing.service.domain.entity.vo.ExcelFailHeadVo;
import com.xwbing.service.domain.entity.vo.ExcelHeaderVo;
import com.xwbing.service.domain.entity.vo.ExcelProcessVo;
import com.xwbing.service.enums.ImportStatusEnum;
import com.xwbing.service.exception.BusinessException;
import com.xwbing.service.util.Jackson;
import com.xwbing.service.util.ThreadUtil;

import cn.hutool.core.util.IdUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author daofeng
 * @version $Id$
 * @since 2021年10月19日 10:43 AM
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class DemoExcelDealService {
    public static final String FAIL_LOG = "boot:excel:failLog:%s";
    private static final String IMPORT_TASK = "boot:excel:task:%s";
    private static final String IMPORT_TASK_ALL = "boot:excel:task*";
    private static final String DEAL_COUNT = "boot:excel:dealCount:%s";
    private final RedisTemplate<String, Object> redisTemplate;

    public boolean checkExport() {
        Set<String> keys = redisTemplate.keys(IMPORT_TASK_ALL);
        if (CollectionUtils.isNotEmpty(keys)) {
            return keys.stream().map(key -> Optional.ofNullable(redisTemplate.opsForValue().get(key))
                    .map(o -> Jackson.build().readValue(Jackson.build().writeValueAsString(o), ImportTask.class))
                    .map(ImportTask::getStatus).orElse(null)).filter(Objects::nonNull)
                    .anyMatch(ImportStatusEnum.EXPORT::equals);
        } else {
            return false;
        }
    }

    public String readByStream(MultipartFile file, String userName) {
        // 校验同个组织是否有在导入中
        boolean checkOrg = checkExport();
        if (checkOrg) {
            throw new BusinessException("上一份数据正在导入中，请等待…");
        }
        String filename = file.getOriginalFilename();
        if (StringUtils.isEmpty(filename)) {
            throw new BusinessException("请选择文件");
        }
        String type = filename.substring(filename.lastIndexOf("."));
        if (!(ExcelTypeEnum.XLSX.getValue().equals(type) || ExcelTypeEnum.XLS.getValue().equals(type))) {
            throw new BusinessException("文件格式不正确");
        }
        String importNo = IdUtil.simpleUUID();
        ImportTask importTask = ImportTask.builder().fileName(filename).status(ImportStatusEnum.EXPORT)
                .needDownload(false).build();
        redisTemplate.opsForValue().set(String.format(IMPORT_TASK, importNo), importTask, 30, TimeUnit.MINUTES);
        try {
            File tmpFile = File.createTempFile("tmp", type);
            FileUtils.copyInputStreamToFile(file.getInputStream(), tmpFile);
            CompletableFuture.runAsync(() -> EasyExcel.read(tmpFile, ExcelHeaderVo.class,
                    new DemoExcelReadListener(importNo, userName, tmpFile, this, redisTemplate))
                            .readCache(new MapCache()).ignoreEmptyRow(Boolean.FALSE).headRowNumber(1).sheet(0).doRead(),
                    ThreadUtil.build().excelThreadPool()).exceptionally(throwable -> {
                log.error("readByStream importNo:{} error", importNo, throwable);
                ImportTask task = (ImportTask)redisTemplate.opsForValue().get(String.format(IMPORT_TASK, importNo));
                if (ImportStatusEnum.EXPORT.equals(task.getStatus())) {
                    this.updateImportTask(importNo,
                            ImportTask.builder().status(ImportStatusEnum.FAIL).detail("系统异常，请重新导入").build());
                }
                throw new BusinessException(throwable);
            });
        } catch (Exception e) {
            log.error("readByStream importNo:{} error", importNo, e);
            this.updateImportTask(importNo,
                    ImportTask.builder().status(ImportStatusEnum.FAIL).detail("系统异常，请重新导入").build());
        }
        return importNo;
    }

    public ExcelProcessVo getProcess(String importNo) {
        ImportTask importTask = (ImportTask)redisTemplate.opsForValue().get(String.format(IMPORT_TASK, importNo));
        if (importTask == null) {
            throw new BusinessException("导入任务不存在");
        }
        Integer totalCount = Optional.ofNullable(importTask.getTotalCount()).orElse(0);
        Integer failCount = Optional.ofNullable(importTask.getFailCount()).orElse(0);
        Integer successCount = totalCount - failCount;
        ImportStatusEnum status = importTask.getStatus();
        if (ImportStatusEnum.FAIL.equals(status)) {
            return ExcelProcessVo.builder().process(100).errorCount(failCount).successCount(successCount)
                    .msg(importTask.getDetail()).success(false).build();
        } else {
            int process;
            if (ImportStatusEnum.SUCCESS.equals(status)) {
                process = 100;
            } else if (totalCount.equals(0)) {
                process = 0;
            } else {
                Object deal = redisTemplate.opsForValue().get(String.format(DEAL_COUNT, importNo));
                int dealCount = deal == null ? 0 : (int)deal;
                process = new BigDecimal(dealCount).divide(new BigDecimal(totalCount), 2, BigDecimal.ROUND_HALF_UP)
                        .multiply(new BigDecimal(100)).intValue();
                //特殊处理，100时，前端不会再请求接口问题
                process = process == 100 ? 99 : process;
            }
            return ExcelProcessVo.builder().process(process).errorCount(importTask.getFailCount())
                    .successCount(successCount).msg(importTask.getDetail()).success(true).build();
        }
    }

    public void export(HttpServletResponse response, String importNo) {
        ImportTask importTask = (ImportTask)redisTemplate.opsForValue().get(String.format(IMPORT_TASK, importNo));
        if (importTask == null) {
            throw new BusinessException("导入任务不存在");
        }
        List<ExcelFailHeadVo> excelData = new ArrayList<>();
        String notice = "导入说明：\n" + "1、文件必须为xls或xlsx格式； \n" + "2、必填字段不允许为空；\n" + "3、请不要修改列名，或者增加其他列；\n"
                + "4、每次导入数据量不允许超过5000条；";
        excelData.add(ExcelFailHeadVo.builder().name(notice).build());
        redisTemplate.opsForList().range(String.format(FAIL_LOG, importNo), 0, -1).forEach(log -> {
            ImportFailLog importFailLog = Jackson.build()
                    .readValue(Jackson.build().writeValueAsString(log), ImportFailLog.class);
            ExcelFailHeadVo headVo = Jackson.build().readValue(importFailLog.getContent(), ExcelFailHeadVo.class);
            headVo = headVo.toBuilder().remark(importFailLog.getRemark()).build();
            excelData.add(headVo);
        });
        if (CollectionUtils.isNotEmpty(excelData)) {
            try (ServletOutputStream outputStream = response.getOutputStream()) {
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/vnd.ms-excel;charset=utf-8");
                //防止中文乱码
                String fileName = URLEncoder.encode(importTask.getFileName(), "UTF-8");
                response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
                response.setHeader("Pragma", "No-cache");
                response.setHeader("Cache-Control", "no-cache");
                response.setDateHeader("Expires", 0);
                EasyExcel.write(outputStream, ExcelFailHeadVo.class).autoTrim(Boolean.TRUE).sheet("sheet0")
                        .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()).doWrite(excelData);
            } catch (Exception e) {
                log.error("writeToBrowser importNo:{} error", importNo, e);
                throw new BusinessException("下载文件失败");
            }
        } else {
            log.error("writeToBrowser importNo:{} no fail data", importNo);
            throw new BusinessException("无失败数据");
        }
    }

    public void updateImportTask(String importNo, ImportTask task) {
        ImportTask redisTask = (ImportTask)redisTemplate.opsForValue().get(String.format(IMPORT_TASK, importNo));
        if (task.getTotalCount() != null) {
            redisTask.setTotalCount(task.getTotalCount());
        }
        if (task.getFailCount() != null) {
            redisTask.setFailCount(task.getFailCount());
        }
        if (task.getStatus() != null) {
            redisTask.setStatus(task.getStatus());
        }
        if (StringUtils.isNotEmpty(task.getDetail())) {
            redisTask.setDetail(task.getDetail());
        }
        if (task.getNeedDownload() != null) {
            redisTask.setNeedDownload(task.getNeedDownload());
        }
        redisTemplate.opsForValue().set(String.format(IMPORT_TASK, importNo), redisTask, 30, TimeUnit.MINUTES);
    }

    public ImportTask getImportTask(String importNo) {
        return (ImportTask)redisTemplate.opsForValue().get(String.format(IMPORT_TASK, importNo));
    }

    public void dealExcelData(ExcelHeaderVo data, String importNo, String userName) {
        log.info("dealExcelData importNo:{}", importNo);
        try {
            // 数据校验
            StringBuilder errorInfo = new StringBuilder();
            if (StringUtils.isNotEmpty(errorInfo.toString())) {
                redisTemplate.opsForList().rightPush(String.format(FAIL_LOG, importNo),
                        ImportFailLog.builder().content(Jackson.build().writeValueAsString(data))
                                .remark(errorInfo.toString()).build());
            } else {
                //保存正确数据
            }
        } catch (Exception e) {
            log.error("dealExcelData importNo:{} error", importNo, e);
        } finally {
            redisTemplate.opsForValue().increment(String.format(DEAL_COUNT, importNo), 1);
            redisTemplate.expire(String.format(DEAL_COUNT, importNo), 30, TimeUnit.MINUTES);
        }
    }
}
