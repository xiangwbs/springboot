package com.xwbing.service.service.rest;

import java.io.File;
import java.math.BigDecimal;
import java.net.URLEncoder;
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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.cache.MapCache;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.fastjson.JSONObject;
import com.xwbing.service.domain.entity.rest.ImportFailLog;
import com.xwbing.service.domain.entity.rest.ImportTask;
import com.xwbing.service.domain.entity.vo.ExcelFailHeadVo;
import com.xwbing.service.domain.entity.vo.ExcelHeaderVo;
import com.xwbing.service.domain.entity.vo.ExcelProcessVo;
import com.xwbing.service.enums.ImportStatusEnum;
import com.xwbing.service.exception.BusinessException;
import com.xwbing.service.exception.ExcelException;
import com.xwbing.service.util.RestMessage;
import com.xwbing.service.util.SensitiveWordEngine;
import com.xwbing.service.util.ThreadUtil;
import com.xwbing.starter.redis.RedisService;

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
    public void writeToBrowser(HttpServletResponse response, String importId) {
        ImportTask importTask = importTaskService.getById(importId);
        if (importTask == null) {
            throw new BusinessException("导入任务不存在");
        }
        List<ImportFailLog> importFailLogs = importFailLogService.listByImportId(importId);
        List<ExcelFailHeadVo> excelData = importFailLogs.stream().map(importFailLog -> {
            ExcelFailHeadVo headVo = JSONObject.parseObject(importFailLog.getContent(), ExcelFailHeadVo.class);
            return headVo.toBuilder().remark(importFailLog.getRemark()).build();
        }).collect(Collectors.toList());
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
                        .doWrite(excelData);
            } catch (Exception e) {
                log.error("writeToBrowser importId:{} error", importId, e);
                throw new BusinessException("下载文件失败");
            }
        } else {
            log.error("writeToBrowser importId:{} no fail data", importId);
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
     * @param file
     * @param sheetNo
     * @param headRowNum 如果从0开始 会读取到表头数据 默认为1
     *
     * @return
     */
    public String readByStream(MultipartFile file, int sheetNo, int headRowNum) {
        String filename = file.getOriginalFilename();
        if (StringUtils.isEmpty(filename)) {
            throw new ExcelException("请选择文件");
        }
        String type = filename.substring(filename.lastIndexOf("."));
        if (!(ExcelTypeEnum.XLSX.getValue().equals(type) || ExcelTypeEnum.XLS.getValue().equals(type))) {
            throw new ExcelException("文件格式不正确");
        }
        ImportTask importTask = ImportTask.builder().fileName(filename).status(ImportStatusEnum.EXPORT)
                .needDownload(false).build();
        RestMessage restMessage = importTaskService.save(importTask);
        String importId = restMessage.getId();
        try {
            //将上传文件复制到自定义临时文件,提高效率。用默认临时文件，多线程高并发下会出现FileNotFoundException
            File tmpFile = File.createTempFile(filename, type);
            FileUtils.copyInputStreamToFile(file.getInputStream(), tmpFile);
            CompletableFuture.runAsync(() -> EasyExcel.read(tmpFile, ExcelHeaderVo.class,
                    new EasyExcelReadListener(importId, tmpFile, this, importTaskService, importFailLogService))
                    .readCache(new MapCache()).ignoreEmptyRow(Boolean.FALSE).headRowNumber(headRowNum).sheet(sheetNo)
                    .doRead(), ThreadUtil.build().excelThreadPool()).exceptionally(throwable -> {
                log.error("readByStream importId:{} error", importId, throwable);
                ImportTask task = importTaskService.getById(importId);
                if (ImportStatusEnum.EXPORT.equals(task.getStatus())) {
                    importTaskService.updateExceptionFail(importId);
                }
                throw new ExcelException(throwable);
            });
        } catch (Exception e) {
            log.error("readByStream importId:{} error", importId, e);
            importTaskService.updateExceptionFail(importId);
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
            throw new ExcelException("导入任务不存在");
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
    public void dealExcelData(List<ExcelHeaderVo> list, String importId) {
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
}