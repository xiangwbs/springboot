package com.xwbing.service.demo;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ReUtil;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.xwbing.service.demo.sql.JdbcUtil;
import com.xwbing.service.util.excel.ExcelUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author daofeng
 * @version $
 * @since 2024年08月12日 5:05 PM
 */
@Slf4j
public class NationalExcelHelper {
    public static void main(String[] args) {
        readExcel("/Users/xwbing/Documents/work/孚嘉/ai问数/国家统计数据/年度数据", "chat_bi_national_year");
    }

    /**
     * excel表名 分类路径.xlsx
     * excel表头结构 指标 时间1 时间2 时间n
     *
     * @param path      所有数据的文件夹包路径
     * @param tableName 数据库表名
     */
    public static void readExcel(String path, String tableName) {
        List<String> fileNames = FileUtil.listFileNames(path);
        int total = fileNames.size();
        log.info("readExcel path:{} total:{}", path, total);
        if (CollectionUtils.isEmpty(fileNames)) {
            return;
        }
        AtomicInteger index = new AtomicInteger();
        fileNames.forEach(fileName -> {
            if (".DS_Store".equals(fileName)) {
                return;
            }
            String categoryPath = fileName.replace(ExcelTypeEnum.XLSX.getValue(), "");
            Map<String, Map<Integer, String>> headMap = new HashMap<>();
            log.info("readExcel fileName:{} total:{} index:{} ", fileName, total, index.incrementAndGet());
            Integer count = ExcelUtil.read(FileUtil.getInputStream(path + "/" + fileName), 0, 1, 500,
                    head -> headMap.put("head", head),
                    data -> {
                        Map<Integer, String> head = headMap.get("head");
                        List<String> valueList = data.stream()
                                .map(excel -> {
                                    String measureText = excel.remove(0);
                                    measureText = measureText.replace("\n", "");
                                    String measure;
                                    String dataUnit;
                                    // 例子:各项税收(亿元)
                                    String unit = ReUtil.getGroup0("\\([^)]+\\)$", measureText);
                                    if (StringUtils.isNotEmpty(unit)) {
                                        measure = measureText.replace(unit, "");
                                        dataUnit = unit.replaceAll("[()]", "");
                                    } else {
                                        measure = measureText;
                                        dataUnit = null;
                                    }
                                    return excel.entrySet().stream()
                                            .map(entry -> {
                                                String value = entry.getValue();
                                                if (StringUtils.isEmpty(value)) {
                                                    return null;
                                                }
                                                String date = head.get(entry.getKey());
                                                ArrayList<String> list = ListUtil.toList(categoryPath, measure, value, dataUnit, date, "系统");
                                                List<String> collect = list.stream().map(v -> {
                                                    if (StringUtils.isNotEmpty(v)) {
                                                        v = "'" + v + "'";
                                                    }
                                                    return v;
                                                }).collect(Collectors.toList());
                                                return "(" + String.join(",", collect) + ")";
                                            })
                                            .filter(Objects::nonNull)
                                            .collect(Collectors.toList());
                                })
                                .flatMap(Collection::stream)
                                .collect(Collectors.toList());
                        if (CollectionUtils.isNotEmpty(valueList)) {
                            String sql = "INSERT INTO " + tableName + "(CATEGORY_PATH,MEASURE,DATA,DATA_UNIT,STATISTICS_DATE,CREATOR) VALUES " + String.join(",", valueList);
                            try {
                                log.info("readExcel fileName:{} sql:{}", fileName, sql);
                                JdbcUtil.upsertSql("jdbc:mysql://127.0.0.1:3306/fugle", "root", "xiangwbs", sql);
                            } catch (Exception e) {
                                if (e instanceof SQLIntegrityConstraintViolationException) {
                                    log.error("readExcel fileName:{} sql:{} duplicateKeyError", fileName, sql);
                                } else {
                                    log.error("readExcel fileName:{} sql:{} error", fileName, sql, e);
                                }
                            }
                        }
                    });
            log.info("readExcel fileName:{} count:{}", fileName, count);
        });
        log.info("readExcel path:{} end", path);
    }
}
