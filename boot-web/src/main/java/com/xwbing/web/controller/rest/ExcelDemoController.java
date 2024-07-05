package com.xwbing.web.controller.rest;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.xwbing.service.domain.entity.vo.ExcelHeaderDemoVo;
import com.xwbing.service.domain.entity.vo.ExcelHeaderVo;
import com.xwbing.service.util.ExcelUtil;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
@Api(tags = "excelDemo", description = "excelDemo接口")
@RestController
@RequestMapping("/excel/")
public class ExcelDemoController {
    private final OssService ossService;

    @PostMapping("read")
    public ApiResponse readExcel(@RequestParam MultipartFile file) throws IOException {
        AtomicInteger count = new AtomicInteger();
        Integer allCount = ExcelUtil.read(file.getInputStream(), ExcelHeaderDemoVo.class, 0, 10, data -> {
            log.info("dealExcel count:{} size:{}", count.incrementAndGet(), data.size());
            data.forEach(d -> log.info("dealExcel row:{}", d));
        });
        log.info("readProductExcel allCount:{}", allCount);
        return ApiResponseUtil.success();
    }

    @ApiOperation("下载excel到浏览器")
    @GetMapping("writeToBrowser")
    public void writeToBrowser(HttpServletResponse response) {
        ExcelUtil.write(response, ExcelHeaderVo.class, "人员名单统计.xlsx", null, pageNumber -> {
            if (pageNumber == 1) {
                return Collections.emptyList();
            }
            //模拟分页
            // PageHelper.startPage(pageNumber, 500);
            List<ExcelHeaderVo> excelData = new ArrayList<>();
            ExcelHeaderVo data = ExcelHeaderVo.builder().name("巷子").age(18).tel("13488888888")
                    .introduction("这是一条简介").build();
            ExcelHeaderVo data1 = ExcelHeaderVo.builder().name("道风").age(18).tel("13488888888")
                    .introduction("这是一条简介").build();
            ExcelHeaderVo data2 = ExcelHeaderVo.builder().name("兵哥").age(18).tel("13488888888")
                    .introduction("这是一条简介").build();
            ExcelHeaderVo data3 = ExcelHeaderVo.builder().name("西门吹雪").age(18).tel("13488888888")
                    .introduction("这是一条简介").build();
            excelData.add(data);
            excelData.add(data1);
            excelData.add(data2);
            excelData.add(data3);
            return excelData;
        });
    }

    @ApiOperation("下载简单动态excel到浏览器")
    @GetMapping("writeSimpleDynamicToBrowser")
    public void writeSimpleDynamicToBrowser(HttpServletResponse response) {
        List<Object> dataList = new ArrayList<>();
        dataList.add("巷子");
        dataList.add(18);
        dataList.add("13488888888");
        dataList.add("这是一条简介");
        List<List<String>> head = Stream.of("姓名", "年龄", "电话", "简介").map(Collections::singletonList).collect(Collectors.toList());
        ExcelUtil.write(response, head, "人员名单统计.xlsx", null, Collections.singletonList(dataList));
    }

    @ApiOperation("下载复杂动态excel到浏览器")
    @GetMapping("writeComplexDynamicToBrowser")
    public void writeComplexDynamicToBrowser(HttpServletResponse response) {
        List<List<String>> head = new ArrayList<>();
        head.add(ListUtil.toList("销售方式", "母公司"));
        head.add(ListUtil.toList("销售方式", "公司"));
        head.add(ListUtil.toList("销售方式", "销售渠道"));
        head.add(ListUtil.toList("自运营", "收入目标（万元）"));
        head.add(ListUtil.toList("自运营", "收入金额（万元）"));
        head.add(ListUtil.toList("代运营", "收入目标（万元）"));
        head.add(ListUtil.toList("代运营", "收入金额（万元）"));
        List<Object> dataList = new ArrayList<>();
        dataList.add("想象力无限公司");
        dataList.add("快乐无限公司");
        dataList.add("电商");
        dataList.add(1000);
        dataList.add(100);
        dataList.add(50);
        dataList.add(30);
        ExcelUtil.write(response, head, "销售统计.xlsx", null, Collections.singletonList(dataList));
    }

    @ApiOperation("下载excel到本地")
    @GetMapping("writeToLocal")
    public void writeToLocal() {
        ExcelUtil.write("/Users/xwbing/Documents", ExcelHeaderVo.class, "人员名单统计.xlsx", null, pageNumber -> {
            if (pageNumber == 5) {
                return Collections.emptyList();
            }
            //模拟分页
            // PageHelper.startPage(pageNumber, 500);
            List<ExcelHeaderVo> excelData = new ArrayList<>();
            ExcelHeaderVo data = ExcelHeaderVo.builder().name("巷子").age(18).tel("13488888888")
                    .introduction("这是一条简介").build();
            ExcelHeaderVo data1 = ExcelHeaderVo.builder().name("道风").age(18).tel("13488888888")
                    .introduction("这是一条简介").build();
            ExcelHeaderVo data2 = ExcelHeaderVo.builder().name("兵哥").age(18).tel("13488888888")
                    .introduction("这是一条简介").build();
            ExcelHeaderVo data3 = ExcelHeaderVo.builder().name("西门吹雪").age(18).tel("13488888888")
                    .introduction("这是一条简介").build();
            excelData.add(data);
            excelData.add(data1);
            excelData.add(data2);
            excelData.add(data3);
            return excelData;
        });
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
                ExcelUtil.write(FileUtil.getTmpDirPath(), ExcelHeaderVo.class, fileName, null,
                        pageNo -> {
                            log.info("writeToOss pageNo:{}", pageNo);
                            if (pageNo == 2) {
                                return Collections.emptyList();
                            }
                            List<ExcelHeaderVo> excelData = new ArrayList<>();
                            ExcelHeaderVo data = ExcelHeaderVo.builder().name("巷子").age(18).tel("13488888888")
                                    .introduction("这是一条简介").build();
                            excelData.add(data);
                            return excelData;
                        });
                log.info("writeToOss putOss");
                ossService.putFile(IoUtil.toStream(tmpFile), ContentTypeEnum.FILE.getCode(),
                        ExcelTypeEnum.XLSX.getValue());
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