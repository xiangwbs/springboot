package com.xwbing.web.controller.rest;

import cn.hutool.core.io.IoUtil;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.fastjson.JSONObject;
import com.xwbing.service.demo.RedisTemplateDemo;
import com.xwbing.service.demo.dingtalk.DingMarkdown;
import com.xwbing.service.demo.dingtalk.DingtalkRobotHelper;
import com.xwbing.service.demo.dingtalk.DingtalkRobotMsg;
import com.xwbing.service.domain.entity.model.NullModel;
import com.xwbing.service.domain.entity.rest.FilesUpload;
import com.xwbing.service.domain.entity.sys.SysUser;
import com.xwbing.service.domain.entity.vo.ExcelHeaderDemoVo;
import com.xwbing.service.domain.entity.vo.ExcelHeaderVo;
import com.xwbing.service.domain.mapper.rest.DynamicMapper;
import com.xwbing.service.enums.SexEnum;
import com.xwbing.service.exception.BusinessException;
import com.xwbing.service.service.rest.CookieSessionService;
import com.xwbing.service.service.rest.EasyExcelDealService;
import com.xwbing.service.service.rest.QRCodeZipService;
import com.xwbing.service.service.rest.UploadService;
import com.xwbing.service.util.*;
import com.xwbing.service.util.dingtalk.DingTalkUtil;
import com.xwbing.service.util.dingtalk.LinkMessage;
import com.xwbing.service.util.dingtalk.MarkdownMessage;
import com.xwbing.starter.aliyun.oss.OssService;
import com.xwbing.starter.aliyun.oss.enums.ContentTypeEnum;
import com.xwbing.starter.aspect.annotation.ReqLimit;
import com.xwbing.starter.operatelog.annotation.OperateLog;
import com.xwbing.starter.spring.ApplicationContextHelper;
import com.xwbing.web.response.ApiResponse;
import com.xwbing.web.response.ApiResponseUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
    @Resource
    private OssService ossService;
    @Resource
    private RedisTemplateDemo redisTemplateDemo;
    @Resource
    private DynamicMapper dynamicMapper;
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

    @ApiOperation("pdf转图片")
    @GetMapping("urlToImage")
    public void pdfConvert(HttpServletResponse response, @RequestParam String url) throws IOException {
        byte[] bytes = PdfUtil.urlToImage(url);
        if (bytes != null) {
            response.setContentType("image/jpeg");
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expire", 0);
            OutputStream out = response.getOutputStream();
            out.write(bytes);
        }
    }

    @ApiOperation("pdf转图片")
    @PostMapping("fileToImage")
    public void pdfConvert(HttpServletResponse response, @RequestParam MultipartFile file) throws IOException {
        byte[] bytes = PdfUtil.fileToImage(file);
        if (bytes != null) {
            response.setContentType("image/jpeg");
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expire", 0);
            OutputStream out = response.getOutputStream();
            out.write(bytes);
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
                .messageUrl(DingtalkRobotHelper.pcSlide("https://www.seniverse.com", true)).build();
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
        message.addItem(
                MarkdownMessage.getLinkText("天气", DingtalkRobotHelper.pcSlide("https://www.seniverse.com", true)));
        message.setAtAll(atAll);
        message.addAtMobiles(atMobiles);
        DingTalkUtil.sendRobotMessage(message);
    }

    @ApiOperation("发送群消息")
    @GetMapping("sendChatLinkMessage")
    public void sendLinkMessage(@RequestParam String token) {
        LinkMessage linkMessage = LinkMessage.builder().chatId("chat0f212fb4dc07478f0813eb98e9470ff6")
                .title("link消息测试").text("消息内容消息内容测试")
                .picUrl("https://gw.alicdn.com/tfs/TB1ut3xxbsrBKNjSZFpXXcXhFXa-846-786.png")
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
                String fileName = cn.hutool.core.io.FileUtil.getName(tmpFile);
                ExcelUtil.write(cn.hutool.core.io.FileUtil.getTmpDirPath(), ExcelHeaderVo.class, fileName, null,
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

    @GetMapping("nullModel")
    public NullModel nullModel() {
        return NullModel.builder().string("字符串").sexEnum(SexEnum.MAN).build();
    }

    @ReqLimit(value = "#p0", timeout = 60)
    // @Limit("#key")
    @GetMapping("limit")
    public void limit(@RequestParam String key) {
        if (key.equals("key")) {
            throw new RuntimeException("异常");
        }
    }

    @GetMapping("completableFuture")
    public JSONObject completableFuture() {
        CompletableFuture[] futures = new CompletableFuture[2];
        futures[0] = CompletableFuture.runAsync(() -> {
            System.out.println("111");
        });
        futures[1] = CompletableFuture.runAsync(() -> {
            if (1 == 1) {
                throw new BusinessException("异步错误");
            }
        });
        CompletableFuture.allOf(futures).join();
        System.out.println("22222");
        return JsonResult.toJSONObj("异步正常", "");
    }

    @PostMapping("readExcel")
    public ApiResponse readExcel(@RequestParam MultipartFile file) throws IOException {
        AtomicInteger count = new AtomicInteger();
        Integer allCount = ExcelUtil.read(file.getInputStream(), ExcelHeaderDemoVo.class, 0, 10, data -> {
            log.info("dealExcel count:{} size:{}", count.incrementAndGet(), data.size());
            data.forEach(d -> log.info("dealExcel row:{}", d));
        });
        log.info("readProductExcel allCount:{}", allCount);
        return ApiResponseUtil.success();
    }

    // @OperateLog(tag = "测试日志", content = "自定义函数1:{exampleFunction{#name}} 自定义函数2:{exampleFunction{#password}} {{#sex}}")
    // @OperateLog(tag = "测试日志", content = "'官方SpEL取值:'+#sex")
    // @OperateLog(tag = "测试日志", content = "自定义SpEL取值:{{#sex}}")
    @OperateLog(tag = "测试日志", content = "执行结果:{{#_result.name}} 错误结果:{{#_errMsg}} 操作人:{{#_operator}}")
    @GetMapping("/testLog")
    public SysUser testLog(@RequestParam String name, @RequestParam String password, @RequestParam String sex) {
        SysUser user = new SysUser();
        user.setName(name);
        user.setPassword(password);
        if ("1".equals(sex)) {
            throw new RuntimeException("error");
        }
        return user;
    }

    @RequestMapping("/robots")
    public String robots(@RequestBody(required = false) JSONObject msg, @RequestHeader(required = false) String sign,
            @RequestHeader(required = false) String timestamp) {
        DingtalkRobotMsg robotMsg = DingtalkRobotHelper.receiveMsg(msg, sign, timestamp);
        if (robotMsg == null) {
            return null;
        }
        // 单聊
        if (robotMsg.getConversationType() == 1) {
            DingtalkRobotHelper.sendText(robotMsg.getClient(), false, null, robotMsg.getContent());

            List<String> orderedList = new ArrayList<>();
            orderedList.add(DingtalkRobotHelper.dtmdLink("回复1"));
            orderedList.add(DingtalkRobotHelper.dtmdLink("回复2"));
            DingMarkdown dingMarkdown = DingMarkdown.build().appendOrderedList(orderedList);
            DingtalkRobotHelper.sendMarkdown(robotMsg.getClient(), false, null, "markdown消息", dingMarkdown);
        }
        // 群聊
        else {
            DingtalkRobotHelper.sendText(robotMsg.getClient(), false,
                    Collections.singletonList(robotMsg.getSenderStaffId()), robotMsg.getContent());

            DingtalkRobotHelper.sendActionCard(robotMsg.getClient(), false,
                    Collections.singletonList(robotMsg.getSenderStaffId()), "actionCard消息",
                    DingMarkdown.build().appendText("这是一个整体跳转actionCard消息"), "https://www.baidu.com");

            List<String> orderedList = new ArrayList<>();
            orderedList.add(DingtalkRobotHelper.dtmdLink("回复1"));
            orderedList.add(DingtalkRobotHelper.dtmdLink("回复2"));
            DingMarkdown dingMarkdown = DingMarkdown.build().appendOrderedList(orderedList);
            DingtalkRobotHelper.sendMarkdown(robotMsg.getClient(), false,
                    Collections.singletonList(robotMsg.getSenderStaffId()), "markdown消息", dingMarkdown);
        }
        return null;
    }

    @GetMapping("/redis/bit")
    public void bit(){
        redisTemplateDemo.bit();
    }

    @GetMapping("/selectBySql")
    public List<Map<String, Object>> selectBySql(){
        return dynamicMapper.selectBySql("select * from sys_user_info");
    }
}