package com.xwbing.web.service;

import javax.annotation.Resource;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.xwbing.service.service.rest.MailService;
import com.xwbing.service.util.RestMessage;
import com.xwbing.web.BaseTest;

import lombok.extern.slf4j.Slf4j;

/**
 * @author daofeng
 * @version $Id$
 * @since 2020年08月24日 下午5:41
 */
@Slf4j
public class MailTest extends BaseTest {
    @Resource
    private MailService mailService;

    /**
     * 发送纯文本邮件
     */
    @Test
    public void sendSimpleMail() {
        RestMessage restMessage = mailService.sendSimpleMail("测试邮件", "收到一个纯文本邮件", null, "786461501@qq.com");
        Assertions.assertTrue(restMessage.isSuccess());
    }

    /**
     * 发送html格式邮件
     */
    @Test
    public void sendHtmlMail() {
        String content = "<html>" + "<body>" + "<h3>hello world ! 这是一封Html邮件!</h3>" + "</body>" + "</html>";
        RestMessage restMessage = mailService.sendHtmlMail("html测试邮件", content, null, "786461501@qq.com");
        Assertions.assertTrue(restMessage.isSuccess());
    }

    /**
     * 发送带附件邮件
     */
    @Test
    public void sendAttachmentsMail() {
        // ClassPathResource file = new ClassPathResource("file");
        // String path = file.getFile().getAbsolutePath();
        String[] paths = { "/Users/xwbing/Documents/异常企业.xlsx", "/Users/xwbing/Documents/异常企业.xlsx" };
        // String path2 = path + "\\QRCode.png";
        RestMessage restMessage = mailService
                .sendAttachmentsMail("附件测试邮件", "收到一个带附件邮件", paths, null, "786461501@qq.com");
        Assertions.assertTrue(restMessage.isSuccess());
    }

    /**
     * 发送文本内嵌图片邮件
     */
    @Test
    public void sendInlineResourceMail() {
        String rscId = "file";
        String content = "<html><body>这是有图片的邮件：<img src=\'cid:" + rscId + "\'></body></html>";
        String imgPath = "C:/Users/admin/Desktop/pic/0000001.png";
        RestMessage restMessage = mailService
                .sendInlineResourceMail("内嵌图片测试邮件", content, imgPath, rscId, null, "786461501@qq.com");
        Assertions.assertTrue(restMessage.isSuccess());
    }
}
