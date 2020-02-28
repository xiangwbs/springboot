package com.xwbing.controller.rest;

import com.alibaba.fastjson.JSONObject;
import com.xwbing.annotation.LogInfo;
import com.xwbing.service.rest.MailService;
import com.xwbing.util.JsonResult;
import com.xwbing.util.RestMessage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * 项目名称: sb-boot-module-pro
 * 创建时间: 2017/9/18 13:42
 * 作者: xiangwb
 * 说明:邮件测试控制层
 */
@Api(tags = "mailApi", description = "邮件测试相关接口")
@RestController
@RequestMapping("/mailtest/")
public class MailTestControl {
    @Resource
    private MailService mailService;

    @LogInfo("发送纯文本邮件")
    @GetMapping("sendSimpleMail")
    @ApiOperation(value = "发送纯文本邮件")
    public JSONObject sendSimpleMail() {
        RestMessage restMessage = mailService.sendSimpleMail("786461501@qq.com", "测试邮件", "收到一个纯文本邮件");
        return JsonResult.toJSONObj(restMessage);
    }

    @LogInfo("发送html格式邮件")
    @GetMapping("sendHtmlMail")
    @ApiOperation(value = "发送html格式邮件")
    public JSONObject sendHtmlMail() {
        String content = "<html>" +
                "<body>" +
                "<h3>hello world ! 这是一封Html邮件!</h3>" +
                "</body>" +
                "</html>";
        RestMessage restMessage = mailService.sendHtmlMail("786461501@qq.com", "html测试邮件", content);
        return JsonResult.toJSONObj(restMessage);
    }

    @LogInfo("发送带附件邮件")
    @GetMapping("sendAttachmentsMail")
    @ApiOperation(value = "发送带附件邮件")
    public JSONObject sendAttachmentsMail() throws IOException {
        ClassPathResource file = new ClassPathResource("file");
        String path = file.getFile().getAbsolutePath();
        String path1 = path + "\\QRCode.png";
        String path2 = path + "\\QRCode.png";
        RestMessage restMessage = mailService.sendAttachmentsMail("786461501@qq.com", "附件测试邮件", "收到一个带附件邮件", path1, path2);
        return JsonResult.toJSONObj(restMessage);
    }

    @LogInfo("发送文本内嵌图片邮件")
    @GetMapping("sendInlineResourceMail")
    @ApiOperation(value = "发送文本内嵌图片邮件")
    public JSONObject sendInlineResourceMail() {
        String rscId = "file";
        String content = "<html><body>这是有图片的邮件：<img src=\'cid:" + rscId + "\'></body></html>";
        String imgPath = "C:/Users/admin/Desktop/pic/0000001.png";
        RestMessage restMessage = mailService.sendInlineResourceMail("786461501@qq.com", "内嵌图片测试邮件", content, imgPath, rscId);
        return JsonResult.toJSONObj(restMessage);
    }
}
