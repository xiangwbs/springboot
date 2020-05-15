package com.xwbing.controller.rest;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
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
import com.xwbing.config.aliyun.AliYunLog;
import com.xwbing.config.redis.RedisService;
import com.xwbing.config.spring.ApplicationContextHelper;
import com.xwbing.config.util.dingTalk.MarkdownMessage;
import com.xwbing.domain.entity.rest.FilesUpload;
import com.xwbing.service.rest.CookieSessionService;
import com.xwbing.service.rest.EasyExcelDealService;
import com.xwbing.service.rest.QRCodeZipService;
import com.xwbing.service.rest.UploadService;
import com.xwbing.util.EncodeUtil;
import com.xwbing.util.FileUtil;
import com.xwbing.util.JsonResult;
import com.xwbing.util.RestMessage;
import com.xwbing.util.ZipUtil;

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
@Api(tags = "testApi", description = "mock接口")
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
    private RedisService redisService;
    @Resource
    private AliYunLog aliYunLog;
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
    public void urlZip(HttpServletResponse response,@RequestParam String[] urls, @RequestParam String fileName, @RequestParam String path)
            throws IOException {
        //创建临时随机目录
        Path tmp = Files.createTempDirectory("tmp");
        ArrayList<File> files = Arrays.stream(urls).map(url -> {
            String tmpFilePath = url.substring(url.lastIndexOf("/"), url.length());
            return FileUtil.toFile(url, tmp + tmpFilePath);
        }).collect(Collectors.toCollection(ArrayList::new));
        ZipUtil.downloadZip(response,files, path, fileName);
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

    @ApiOperation("redis")
    @GetMapping("redis")
    public JSONObject redis(@RequestParam String kv) {
        redisService.set(kv, kv);
        return JsonResult.toJSONObj(redisService.get(kv), "redis success");
    }

    @ApiOperation("钉钉群发送文本信息")
    @GetMapping("sendTextMessage")
    public void sendTextMessage(@RequestParam boolean atAll, @RequestParam List<String> atMobiles) {
        aliYunLog.sendTextMessage("我是一个文本", atAll, atMobiles, "test");
    }

    @ApiOperation("钉钉群发送markdown信息")
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
        message.addItem(MarkdownMessage.getLinkText("天气", "https://www.seniverse.com"));
        message.setAtAll(atAll);
        message.addAtMobiles(atMobiles);
        aliYunLog.sendMarkdownMessage(message);
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

    @ApiOperation("excel文件下载")
    @GetMapping("download")
    public void exportStream(HttpServletResponse response) {
        List<String> titles = new ArrayList<>();
        titles.add("姓名");
        titles.add("年龄");
        List<List<Object>> excelData = new ArrayList<>();
        List<Object> data = new ArrayList<>();
        data.add("项伟兵");
        data.add(18);
        excelData.add(data);
        easyExcelDealService.download(response, "人员名单统计", "人员名单", null, titles, excelData);
    }

    @ApiOperation("生成excel")
    @GetMapping("write")
    public void write() {
        List<List<Object>> excelData = new ArrayList<>();
        List<Object> data = new ArrayList<>();
        data.add("项伟兵");
        data.add(18);
        data.add("13488888888");
        data.add("这是一条简介");
        excelData.add(data);
        List<Object> data1 = new ArrayList<>();
        data1.add("李四");
        data1.add(18);
        data1.add("13488888888");
        data1.add("法轮功");
        excelData.add(data1);
        List<Object> data2 = new ArrayList<>();
        data2.add(null);
        data2.add(18);
        data2.add("13488888888");
        data2.add("法轮功");
        excelData.add(data2);
        easyExcelDealService.write("/Users/xwbing/Documents", "人员名单统计", "人员名单", null, excelData);
    }

    @ApiOperation("读取excel")
    @GetMapping("read")
    public JSONObject read() {
        String importId = easyExcelDealService.read("/Users/xwbing/Documents/导入模板.xlsx", 0, 1);
        return JsonResult.toJSONObj(importId, "");
    }

    @ApiOperation("test")
    @GetMapping("test")
    public void test() {

    }
}

