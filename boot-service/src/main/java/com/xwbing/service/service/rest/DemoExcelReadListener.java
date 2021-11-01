package com.xwbing.service.service.rest;

import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.read.metadata.holder.ReadSheetHolder;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xwbing.service.domain.entity.rest.ImportTask;
import com.xwbing.service.domain.entity.vo.ExcelHeaderVo;
import com.xwbing.service.enums.ImportStatusEnum;
import com.xwbing.service.exception.BusinessException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author daofeng
 * @version $Id$
 * @since 2020年04月14日 下午9:51
 */
@Slf4j
public class DemoExcelReadListener extends AnalysisEventListener<ExcelHeaderVo> {
    private static final int MAX_COUNT = 5000;
    private static final int SAMPLE_LINES = 2;
    private int totalCount;
    private final String importNo;
    private final String userName;
    private final File tmpFile;
    private final DemoExcelDealService excelDealService;
    private final RedisTemplate<String, Object> redisTemplate;

    public DemoExcelReadListener(String importNo, String userName, File tmpFile, DemoExcelDealService excelDealService,
            RedisTemplate<String, Object> redisTemplate) {
        this.importNo = importNo;
        this.userName = userName;
        this.tmpFile = tmpFile;
        this.excelDealService = excelDealService;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 这个每一条数据解析都会来调用
     *
     * @param data
     * @param context
     */
    @Override
    public void invoke(ExcelHeaderVo data, AnalysisContext context) {
        Integer currentRowNum = context.readRowHolder().getRowIndex();
        log.info("invoke importNo:{} rowNum:{} data:{}", importNo, currentRowNum, JSON.toJSONString(data));
        //不处理示例数据
        if (currentRowNum < SAMPLE_LINES) {
            return;
        }
        dealData(data);
    }

    @Override
    public void onException(Exception exception, AnalysisContext context) {
        deleteTmpFile();
        if (!(exception instanceof BusinessException)) {
            log.error("onException importNo:{} error", importNo, exception);
            excelDealService.updateImportTask(importNo,
                    ImportTask.builder().status(ImportStatusEnum.FAIL).detail("系统异常，请重新导入").build());
            throw new BusinessException("系统异常，导入结束");
        } else {
            throw new BusinessException(exception.getMessage());
        }
    }

    /**
     * 所有数据解析完成了，都会来调用
     *
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("doAfterAllAnalysed importNo:{} start", importNo);
        //更新导入任务
        Integer failSize = Optional
                .ofNullable(redisTemplate.opsForList().size(String.format(DemoExcelDealService.FAIL_LOG, importNo)))
                .map(Long::intValue).orElse(0);
        String detail;
        if (failSize == 0L) {
            detail = "导入成功,本次导入总共数据" + totalCount + "条,成功导入数据" + totalCount + "条";
        } else {
            detail = "导入模板数据有误,本次导入总共数据" + totalCount + "条,成功导入数据" + (totalCount - failSize) + "条,导入失败数据" + failSize
                    + "条";
        }
        ImportTask build = ImportTask.builder().status(ImportStatusEnum.SUCCESS).failCount(failSize).detail(detail)
                .needDownload(failSize != 0L).build();
        excelDealService.updateImportTask(importNo, build);
        deleteTmpFile();
        log.info("doAfterAllAnalysed importNo:{} end", importNo);
    }

    /**
     * 这里会一行行的返回头
     *
     * @param headMap
     * @param context
     */
    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        log.info("invokeHead importNo:{} head:{}", importNo, JSONObject.toJSONString(headMap));
        //获取总条数
        ReadSheetHolder readSheetHolder = context.readSheetHolder();
        Integer totalRowNumber = readSheetHolder.getApproximateTotalRowNumber();
        this.totalCount = totalRowNumber <= SAMPLE_LINES ? 0 : totalRowNumber - SAMPLE_LINES;
        log.info("invokeHeadMap importNo:{} totalCount:{}", importNo, totalCount);
        //校验
        String errorMsg = null;
        if (headMap.size() != 8) {
            errorMsg = "导入模板格式有误，请不要随意修改导入模板的样式，建议重新下载模板";
        } else if (totalCount > MAX_COUNT) {
            errorMsg = "每次导入数据不允许超过5000条";
        } else if (totalCount == 0) {
            errorMsg = "导入模板无数据";
        } else if (!(headMap.get(0).contains("机构名称") && headMap.get(1).contains("统一社会信用代码") && headMap.get(2)
                .contains("法定代表人") && headMap.get(3).contains("地址") && headMap.get(4).contains("电话") && headMap.get(5)
                .contains("信用分") && headMap.get(6).contains("税务机关代码") && headMap.get(7).contains("所属税局"))) {
            errorMsg = "导入模板格式有误，请不要随意修改导入模板的样式，建议重新下载模板";
        }
        if (StringUtils.isNotEmpty(errorMsg)) {
            log.info("invokeHeadMap importId:{} fail:{}", importNo, errorMsg);
            ImportTask fail = ImportTask.builder().status(ImportStatusEnum.FAIL).totalCount(totalCount)
                    .failCount(totalCount).detail(errorMsg).build();
            excelDealService.updateImportTask(importNo, fail);
            throw new BusinessException("导入失败");
        } else {
            ImportTask importTask = ImportTask.builder().totalCount(totalCount).build();
            excelDealService.updateImportTask(importNo, importTask);
        }
        log.info("invokeHeadMap importNo:{} end", importNo);
    }

    /**
     * 处理数据
     */
    private void dealData(ExcelHeaderVo data) {
        log.info("dealExcelData importNo:{}", importNo);
        excelDealService.dealExcelData(data, importNo, userName);
    }

    /**
     * 删除临时文件
     */
    private void deleteTmpFile() {
        if (!Objects.isNull(tmpFile) && tmpFile.exists()) {
            boolean delete = tmpFile.delete();
            log.info("deleteTmpFile importNo:{} {}", importNo, delete);
        }
    }
}
