package com.xwbing.web.controller.rest;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.xwbing.service.domain.entity.vo.ExcelHeaderDemoVo;
import com.xwbing.service.domain.entity.vo.ExcelHeaderVo;
import com.xwbing.service.domain.mapper.rest.DynamicMapper;
import com.xwbing.service.util.excel.ExcelRowMergeStrategy;
import com.xwbing.service.util.excel.ExcelUtil;
import com.xwbing.starter.aliyun.oss.OssService;
import com.xwbing.starter.aliyun.oss.enums.ContentTypeEnum;
import com.xwbing.web.response.ApiResponse;
import com.xwbing.web.response.ApiResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author daofeng
 * @version $
 * @since 2024年07月05日 10:00 AM
 */
@Slf4j
@RequiredArgsConstructor
@Api(tags = "excelDemo接口")
@RestController
@RequestMapping("/excel/")
public class ExcelDemoController {
    private final OssService ossService;
    private final DynamicMapper dynamicMapper;

    @ApiOperation("读取固定头")
    @PostMapping("readExcel")
    public ApiResponse<Integer> readExcel(@RequestParam MultipartFile file) throws IOException {
        AtomicInteger count = new AtomicInteger();
        Integer allCount = ExcelUtil.read(file.getInputStream(), ExcelHeaderDemoVo.class, 0, 10, data -> {
            log.info("dealExcel count:{} size:{}", count.incrementAndGet(), data.size());
            data.forEach(d -> log.info("dealExcel row:{}", d));
        });
        return ApiResponseUtil.success(allCount);
    }

    @ApiOperation("读取动态头")
    @PostMapping("readDynamic")
    public ApiResponse<Integer> readDynamic(@RequestParam MultipartFile file) throws IOException {
        Map<String, Map<Integer, String>> headMap = new HashMap<>();
        Integer count = ExcelUtil.read(file.getInputStream(), 0, 1, 500,
                head -> headMap.put("head", head),
                data -> data.forEach(excel -> {
                    // 表头index从0开始
                    Map<Integer, String> head = headMap.get("head");
                    // key为列索引从0开始
                    excel.forEach((key, value) -> {
                        String column = head.get(key);
                        System.out.println(column + "的值为" + value);
                    });
                }));
        return ApiResponseUtil.success(count);
    }

    @ApiOperation("读取sql数据")
    @PostMapping("readInsertSql")
    public ApiResponse<Integer> readInsertSql(@RequestParam MultipartFile file) throws IOException {
        Map<String, Map<Integer, String>> headMap = new HashMap<>();
        Integer count = ExcelUtil.read(file.getInputStream(), 0, 1, 100,
                head -> headMap.put("head", head),
                data -> {
                    List<String> valueList = data.stream()
                            .map(excel -> {
                                List<String> values = excel.values().stream()
                                        .map(value -> {
                                            if (value != null) {
                                                value = "'" + value + "'";
                                            }
                                            return value;
                                        })
                                        .collect(Collectors.toList());
                                return "(" + String.join(",", values) + ")";
                            }).collect(Collectors.toList());
                    String field = String.join(",", ListUtil.toList(headMap.get("head").values()));
                    String table = StrUtil.subBefore(file.getOriginalFilename(), ".", false);
                    String sql = "INSERT INTO " + table + "(" + field + ") VALUES " + String.join(",", valueList);
                    dynamicMapper.insertBySql(sql);
                });
        return ApiResponseUtil.success(count);
    }

    @ApiOperation("下载excel到本地")
    @GetMapping("writeToLocal")
    public void writeToLocal() {
        ExcelUtil.write("/Users/xwbing/Documents", ExcelHeaderVo.class, "下载excel到本地" + ExcelTypeEnum.XLSX.getValue(), null, pageNumber -> {
            if (pageNumber == 2) {
                return Collections.emptyList();
            }
            //模拟分页
            // PageHelper.startPage(pageNumber, 500);
            List<ExcelHeaderVo> excelData = new ArrayList<>();
            ExcelHeaderVo data = ExcelHeaderVo.builder().name("巷子").age(18).tel("13488888888").introduction("这是一条简介").build();
            excelData.add(data);
            return excelData;
        });
    }

    @ApiOperation("下载excel到浏览器")
    @GetMapping("writeToBrowser")
    public void writeToBrowser(HttpServletResponse response) {
        ExcelUtil.write(response, ExcelHeaderVo.class, "下载excel到浏览器" + ExcelTypeEnum.XLSX.getValue(), null, pageNumber -> {
            if (pageNumber == 2) {
                return Collections.emptyList();
            }
            //模拟分页
            // PageHelper.startPage(pageNumber, 500);
            List<ExcelHeaderVo> excelData = new ArrayList<>();
            ExcelHeaderVo data = ExcelHeaderVo.builder().name("巷子").age(18).tel("13488888888").introduction("这是一条简介").build();
            excelData.add(data);
            return excelData;
        });
    }

    @ApiOperation("下载动态头excel到浏览器")
    @GetMapping("writeDynamicToBrowser")
    public void writeDynamicToBrowser(HttpServletResponse response) {
        List<List<String>> head = Stream.of("姓名", "年龄", "电话", "简介").map(Collections::singletonList).collect(Collectors.toList());
        List<Object> dataList = new ArrayList<>();
        dataList.add("巷子");
        dataList.add(18);
        dataList.add("13488888888");
        dataList.add("这是一条简介");
        ExcelUtil.write(response, head, "下载动态头excel到浏览器" + ExcelTypeEnum.XLSX.getValue(), null, Collections.singletonList(dataList));
    }

    @ApiOperation("下载复杂头excel到浏览器")
    @GetMapping("writeComplexToBrowser")
    public void writeComplexToBrowser(HttpServletResponse response) {
        List<List<String>> head = new ArrayList<>();
        head.add(ListUtil.toList("基础信息", "姓名"));
        head.add(ListUtil.toList("基础信息", "电话"));
        head.add(ListUtil.toList("补充信息", "职业"));
        head.add(ListUtil.toList("补充信息", "爱好"));
        List<List<Object>> dataList = new ArrayList<>();
        List<Object> dataList1 = new ArrayList<>();
        dataList1.add("道风");
        dataList1.add("13111112222");
        dataList1.add("程序员");
        dataList1.add("台球");
        dataList.add(dataList1);
        List<Object> dataList2 = new ArrayList<>();
        dataList2.add("巷子");
        dataList2.add("13412341234");
        dataList2.add("销售");
        dataList2.add("蹦迪");
        dataList.add(dataList2);
        ExcelUtil.write(response, head, "下载复杂头excel到浏览器" + ExcelTypeEnum.XLSX.getValue(), null, dataList);
    }

    @ApiOperation("下载单元格合并excel到浏览器")
    @GetMapping("writeRowMergeToBrowser")
    public void writeRowMergeToBrowser(HttpServletResponse response) {
        List<List<String>> head = Stream.of("姓名", "年龄").map(Collections::singletonList).collect(Collectors.toList());
        List<List<Object>> dataList = new ArrayList<>();
        List<Object> dataList1 = new ArrayList<>();
        dataList1.add("道风");
        dataList1.add(16);
        dataList.add(dataList1);
        List<Object> dataList2 = new ArrayList<>();
        dataList2.add("项伟兵");
        dataList2.add(18);
        dataList.add(dataList2);
        List<Object> dataList3 = new ArrayList<>();
        dataList3.add("项伟兵");
        dataList3.add(19);
        dataList.add(dataList3);
        List<Object> dataList4 = new ArrayList<>();
        dataList4.add("巷子");
        dataList4.add(20);
        dataList.add(dataList4);
        ExcelRowMergeStrategy rowMergeStrategy = new ExcelRowMergeStrategy(0, ListUtil.toList(0));
        ExcelUtil.write(rowMergeStrategy, response, head, "下载单元格合并excel到浏览器" + ExcelTypeEnum.XLSX.getValue(), null, dataList);
    }

    @ApiOperation("下载excel到oss")
    @GetMapping("writeToOss")
    public String writeToOss() {
        String objectKey = ossService.generateObjectKey(ContentTypeEnum.FILE);
        CompletableFuture.runAsync(() -> {
            try {
                log.info("writeToOss");
                File tmpFile = File.createTempFile("writeToOss", ExcelTypeEnum.XLSX.getValue());
                String fileName = FileUtil.getName(tmpFile);
                ExcelUtil.write(FileUtil.getTmpDirPath(), ExcelHeaderVo.class, fileName, null, pageNo -> {
                    log.info("writeToOss pageNo:{}", pageNo);
                    if (pageNo == 2) {
                        return Collections.emptyList();
                    }
                    List<ExcelHeaderVo> excelData = new ArrayList<>();
                    ExcelHeaderVo data = ExcelHeaderVo.builder().name("巷子").age(18).tel("13488888888").introduction("这是一条简介").build();
                    excelData.add(data);
                    return excelData;
                });
                log.info("writeToOss putOss");
                ossService.putFile(IoUtil.toStream(tmpFile), ContentTypeEnum.FILE.getCode(), ExcelTypeEnum.XLSX.getValue());
                if (tmpFile.exists()) {
                    boolean delete = tmpFile.delete();
                    log.info("writeToOss delete tmpFile:{}", delete);
                }
            } catch (Exception e) {
                log.error("writeToOss error", e);
            }
        });
        return ossService.getUrl(objectKey);
    }
}