package com.xwbing.web.controller.rest;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.xwbing.starter.spring.ApplicationContextHelper;
import com.xwbing.service.util.dingtalk.DingTalkUtil;
import com.xwbing.service.util.dingtalk.LinkMessage;
import com.xwbing.service.util.dingtalk.MarkdownMessage;
import com.xwbing.service.domain.entity.model.NullModel;
import com.xwbing.service.domain.entity.rest.FilesUpload;
import com.xwbing.service.domain.entity.vo.ExcelVo;
import com.xwbing.service.enums.SexEnum;
import com.xwbing.service.service.rest.CookieSessionService;
import com.xwbing.service.service.rest.EasyExcelDealService;
import com.xwbing.service.service.rest.QRCodeZipService;
import com.xwbing.service.service.rest.UploadService;
import com.xwbing.service.util.EncodeUtil;
import com.xwbing.service.util.FileUtil;
import com.xwbing.service.util.JsonResult;
import com.xwbing.service.util.RestMessage;
import com.xwbing.service.util.ZipUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * 说明: mock控制层
 * 项目名称: boot-module-pro
 * 创建时间: 2017/5/5 9:21
 * 作者:  xiangwb
 */
@Slf4j
@Api(tags = "mockApi", description = "mock接口")
@RestController
@RequestMapping("/mock/")
public class MockControl {
    @Resource
    private QRCodeZipService qrCodeZipService;
    @Resource
    private CookieSessionService cookieSessionService;
    @Resource
    private UploadService uploadService;
    @Resource
    private EasyExcelDealService easyExcelDealService;
    private List<byte[]> memoryBytes = new ArrayList<>();

    @ApiOperation("导出zip")
    @GetMapping("downloadFileZip")
    public JSONObject downloadFileZip(HttpServletResponse response, @RequestParam String[] names,
            @RequestParam String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return JsonResult.toJSONObj("zip名称不能为空");
        }
        RestMessage restMessage = qrCodeZipService.batchGetImage(response, names, fileName);
        return JsonResult.toJSONObj(restMessage);
    }

    @ApiOperation("urlZip")
    @GetMapping("urlZip")
    public void urlZip(HttpServletResponse response, @RequestParam String[] urls, @RequestParam String fileName,
            @RequestParam String path) throws IOException {
        //创建临时随机目录
        Path tmp = Files.createTempDirectory("tmp");
        ArrayList<File> files = Arrays.stream(urls).map(url -> {
            String tmpFilePath = url.substring(url.lastIndexOf("/"), url.length());
            return FileUtil.urlToFile(url, tmp + tmpFilePath);
        }).collect(Collectors.toCollection(ArrayList::new));
        ZipUtil.downloadZip(response, files, path, fileName);
        Files.delete(tmp);
    }

    @ApiOperation("获取数据库图片")
    @GetMapping("getDbPic")
    public void getDbPic(HttpServletResponse response, @RequestParam String name,
            @RequestParam(required = false) String type) throws IOException {
        if (StringUtils.isNotEmpty(name)) {
            List<FilesUpload> files = uploadService.findByName(name, type);
            if (CollectionUtils.isNotEmpty(files)) {
                String data = files.get(0).getData();
                byte[] bytes = EncodeUtil.base64Decode(data);
                // 设置相应类型,告诉浏览器输出的内容为图片
                response.setContentType("image/jpeg");
                // 禁止图像缓存。
                response.setHeader("Pragma", "No-cache");
                response.setHeader("Cache-Control", "no-cache");
                response.setDateHeader("Expire", 0);
                OutputStream out = response.getOutputStream();
                out.write(bytes);
            }
        }
    }

    @ApiOperation("session")
    @GetMapping("session")
    public JSONObject session(HttpServletRequest request) {
        return JsonResult.toJSONObj(cookieSessionService.session(request));
    }

    @ApiOperation("cookie")
    @GetMapping("cookie")
    public JSONObject cookie(HttpServletRequest request, HttpServletResponse response) {
        return JsonResult.toJSONObj(cookieSessionService.cookie(response, request));
    }

    @ApiOperation("发送钉钉机器人消息")
    @GetMapping("sendTextMessage")
    public void sendTextMessage(@RequestParam boolean atAll, @RequestParam List<String> atMobiles) {
        DingTalkUtil.sendRobotMessage("我是一个文本", atAll, atMobiles, "test");
    }

    @ApiOperation("发送钉钉机器人消息")
    @GetMapping("sendLinkMessage")
    public void sendLinkMessage() {
        LinkMessage linkMessage = LinkMessage.builder().title("通知公告测试").text("查看详情")
                .picUrl("https://gw.alicdn.com/tfs/TB1ut3xxbsrBKNjSZFpXXcXhFXa-846-786.png")
                .messageUrl(DingTalkUtil.toPcSlide("https://www.seniverse.com")).build();
        DingTalkUtil.sendRobotMessage(linkMessage);
    }

    @ApiOperation("发送钉钉机器人消息")
    @GetMapping("sendMarkdownMessage")
    public void sendMarkdownMessage(@RequestParam boolean atAll, @RequestParam List<String> atMobiles) {
        MarkdownMessage message = new MarkdownMessage();
        message.setTitle("markdown message");
        message.addItem(MarkdownMessage.getHeaderText(6, "六级标题"));
        message.addItem(MarkdownMessage.getReferenceText("引用"));
        message.addItem("正常字体");
        message.addItem(MarkdownMessage.getBoldText("加粗字体"));
        message.addItem(MarkdownMessage.getItalicText("斜体"));
        ArrayList<String> orderList = new ArrayList<>();
        orderList.add("有序列表1");
        orderList.add("有序列表2");
        message.addItem(MarkdownMessage.getOrderListText(orderList));
        ArrayList<String> unOrderList = new ArrayList<>();
        unOrderList.add("无序列表1");
        unOrderList.add("无序列表2");
        message.addItem(MarkdownMessage.getUnOrderListText(unOrderList));
        message.addItem(
                MarkdownMessage.getImageText("https://gw.alicdn.com/tfs/TB1ut3xxbsrBKNjSZFpXXcXhFXa-846-786.png"));
        message.addItem(MarkdownMessage.getLinkText("天气", DingTalkUtil.toPcSlide("https://www.seniverse.com")));
        message.setAtAll(atAll);
        message.addAtMobiles(atMobiles);
        DingTalkUtil.sendRobotMessage(message);
    }

    @ApiOperation("发送群消息")
    @GetMapping("sendChatLinkMessage")
    public void sendLinkMessage(@RequestParam String token) {
        LinkMessage linkMessage = LinkMessage.builder().chatId("chat0f212fb4dc07478f0813eb98e9470ff6").title("link消息测试")
                .text("消息内容消息内容测试").picUrl("https://gw.alicdn.com/tfs/TB1ut3xxbsrBKNjSZFpXXcXhFXa-846-786.png")
                .messageUrl("https://www.seniverse.com").build();
        DingTalkUtil.sendChatMessage(linkMessage, token);
    }

    @ApiOperation("发送群消息")
    @GetMapping("sendChatMDMessage")
    public void sendMarkdownMessage(@RequestParam String token) {
        MarkdownMessage message = new MarkdownMessage();
        message.setChatId("chat0f212fb4dc07478f0813eb98e9470ff6");
        message.setTitle("通知公告测试");
        message.addItem(MarkdownMessage.getLinkText("查看详情", "https://www.seniverse.com"));
        DingTalkUtil.sendChatMessage(message, token);
    }

    @ApiOperation("spring上下文")
    @GetMapping("applicationContext")
    public void applicationContext() {
        String port = ApplicationContextHelper.getProperty("server.port", String.class);
        MockControl bean = ApplicationContextHelper.getBean(MockControl.class);
    }

    @ApiOperation("死锁模拟")
    @GetMapping("deadlock")
    public String deadlock() {
        new Thread(() -> {
            synchronized (Integer.class) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                    synchronized (String.class) {
                        System.out.println("获取string锁成功");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }).start();
        synchronized (String.class) {
            try {
                TimeUnit.SECONDS.sleep(1);
                synchronized (Integer.class) {
                    System.out.println("获取integer锁成功");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return "死锁";
    }

    @ApiOperation("cpu100%")
    @GetMapping("loop")
    public String loop() {
        boolean b = true;
        while (b) {
            System.out.println("死循环");
        }
        return "cpu100%";
    }

    @ApiOperation("内存溢出")
    @GetMapping("oom")
    public void oom() {
        List<byte[]> oomBytes = new ArrayList<>();
        while (true) {
            oomBytes.add(new byte[1024 * 2024 * 4]);
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @ApiOperation("内存泄露")
    @GetMapping("memoryLeak")
    public void memoryLeak() {
        while (true) {
            memoryBytes.add(new byte[1024 * 2024 * 4]);
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @ApiOperation("下载excel文件")
    @GetMapping("writeToBrowser")
    public void writeToBrowser(HttpServletResponse response) {
        List<String> titles = new ArrayList<>();
        titles.add("姓名");
        titles.add("年龄");
        List<Object> data = new ArrayList<>();
        data.add("项伟兵");
        data.add("18");
        List<List<Object>> excelData = Collections.singletonList(data);
        easyExcelDealService.writeToBrowser(response, "人员名单统计", "人员名单", null, titles, excelData);
    }

    @ApiOperation("生成excel到本地")
    @GetMapping("writeToLocal")
    public void writeToLocal() {
        List<ExcelVo> excelData = new ArrayList<>();
        ExcelVo data = ExcelVo.builder().name("项伟兵").age(18).tel("13488888888").introduction("这是一条简介").build();
        ExcelVo data1 = ExcelVo.builder().name("项伟兵").age(18).tel("13488888888").introduction("这是一条简介").build();
        ExcelVo data2 = ExcelVo.builder().name("李四").age(18).tel("13488888888").introduction("法轮功").build();
        ExcelVo data3 = ExcelVo.builder().name(null).age(18).tel("13488888888").introduction("法轮功").build();
        excelData.add(data);
        excelData.add(data1);
        excelData.add(data2);
        excelData.add(data3);
        easyExcelDealService.writeToLocal("/Users/xwbing/Documents", "人员名单统计", "人员名单", null, excelData);
    }

    @ApiOperation("生成excel到本地")
    @GetMapping("writeToLocalByPage")
    public void writeToLocalByPage() {
        Function<Integer, List<ExcelVo>> dataFunction = pageNumber -> {
            if (pageNumber == 2) {
                return Collections.emptyList();
            }
            //模拟分页
            // PageHelper.startPage(pageNumber, 500);
            List<ExcelVo> excelData = new ArrayList<>();
            ExcelVo data = ExcelVo.builder().name("项伟兵").age(18).tel("13488888888").introduction("这是一条简介").build();
            ExcelVo data1 = ExcelVo.builder().name("项伟兵").age(18).tel("13488888888").introduction("这是一条简介").build();
            ExcelVo data2 = ExcelVo.builder().name("李四").age(18).tel("13488888888").introduction("法轮功").build();
            ExcelVo data3 = ExcelVo.builder().name(null).age(18).tel("13488888888").introduction("法轮功").build();
            excelData.add(data);
            excelData.add(data1);
            excelData.add(data2);
            excelData.add(data3);
            return excelData;
        };
        easyExcelDealService.writeToLocalByPage("/Users/xwbing/Documents", "人员名单统计", "人员名单", null, dataFunction);
    }

    @ApiOperation("生成多个sheet到本地")
    @GetMapping("repeatedWriteToLocal")
    public void repeatedWriteToLocal() {
        easyExcelDealService.repeatedWriteToLocal("/Users/xwbing/Documents", "人员名单统计");
    }

    @ApiOperation("从本地读取excel")
    @GetMapping("readByLocal")
    public JSONObject readByLocal() {
        String importId = easyExcelDealService.readByLocal("/Users/xwbing/Documents/导入模板.xlsx", 0, 1);
        return JsonResult.toJSONObj(importId, "");
    }

    @GetMapping("nullModel")
    public NullModel nullModel() {
        return NullModel.builder().string("字符串").sexEnum(SexEnum.MAN).build();
    }
}

