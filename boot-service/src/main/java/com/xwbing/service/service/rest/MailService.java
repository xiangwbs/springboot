package com.xwbing.service.service.rest;

import java.io.File;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.xwbing.service.exception.BusinessException;
import com.xwbing.service.util.RestMessage;

/**
 * 创建时间: 2017/9/18 11:31
 * 作者: xiangwb
 * 说明: mail服务层
 */
@Service
public class MailService {
    @Resource
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String from;

    /**
     * 发送纯文本的简单邮件
     *
     * @param to 收件人
     * @param subject 主题
     * @param content 文本内容
     */
    public RestMessage sendSimpleMail(String subject, String content, String[] cc, String... to) {
        RestMessage restMessage = new RestMessage();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        if (cc != null && cc.length > 0) {
            message.setCc(cc);
        }
        message.setSubject(subject);
        message.setText(content);
        try {
            //发送邮件
            mailSender.send(message);
            restMessage.setSuccess(true);
            restMessage.setMessage("纯文本邮件已经发送");
            return restMessage;
        } catch (MailException e) {
            throw new BusinessException("发送纯文本邮件异常");
        }
    }

    /**
     * 发送html格式的邮件
     *
     * @param to 收件人
     * @param subject 主题
     * @param content 文本内容
     */
    public RestMessage sendHtmlMail(String subject, String content, String[] cc, String... to) {
        RestMessage restMessage = new RestMessage();
        MimeMessage message = mailSender.createMimeMessage();
        getHelper(message, subject, content, cc, to);
        mailSender.send(message);
        restMessage.setSuccess(true);
        restMessage.setMessage("html邮件已经发送!");
        return restMessage;
    }

    /**
     * 发送带附件的邮件
     *
     * @param to 收件人
     * @param subject 主题
     * @param content 文本内容
     * @param rscPaths 附件文件路径
     */
    public RestMessage sendAttachmentsMail(String subject, String content, String[] rscPaths, String[] cc,
            String... to) {
        RestMessage restMessage = new RestMessage();
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = getHelper(message, subject, content, cc, to);
        try {
            //添加附件
            if (rscPaths != null && rscPaths.length > 0) {
                FileSystemResource file;
                for (String rscPath : rscPaths) {
                    file = new FileSystemResource(new File(rscPath));
                    String fileName = rscPath.substring(rscPath.lastIndexOf(File.separator) + 1);
                    helper.addAttachment(MimeUtility.encodeWord(fileName, "utf-8", "B"), file);
                }
            }
            mailSender.send(message);
            restMessage.setSuccess(true);
            restMessage.setMessage("带附件的邮件已经发送!");
            return restMessage;
        } catch (Exception e) {
            throw new BusinessException("发送带附件的邮件时发生异常");
        }
    }

    /**
     * 发送嵌入静态资源（一般是图片）的邮件
     *
     * @param to 收件人
     * @param subject 主题
     * @param content 邮件内容，需要包括一个静态资源的id，比如：<img src='cid:rscId01'>
     * @param rscPath 静态资源路径
     * @param rscId 静态资源id
     */
    public RestMessage sendInlineResourceMail(String subject, String content, String rscPath, String rscId,
            String[] cc, String... to) {
        RestMessage restMessage = new RestMessage();
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = getHelper(message, subject, content, cc, to);
        try {
            //添加多个图片可以使用多条 <img src='cid:" + rscId + "' > 和 helper.addInline(rscId, res) 来实现
            FileSystemResource res = new FileSystemResource(new File(rscPath));
            helper.addInline(rscId, res);
            mailSender.send(message);
            restMessage.setSuccess(true);
            restMessage.setMessage("嵌入静态资源的邮件已经发送!");
            return restMessage;
        } catch (MessagingException e) {
            throw new BusinessException("发送嵌入静态资源的邮件时发生异常！");
        }
    }

    /**
     * 获取邮件助手
     *
     * @param message
     *
     * @return
     */
    private MimeMessageHelper getHelper(MimeMessage message, String subject, String content, String[] cc,
            String... to) {
        try {
            //true表示需要创建一个multipart message
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setFrom(from);
            helper.setTo(to);
            if (cc != null && cc.length > 0) {
                helper.setCc(cc);
            }
            helper.setSubject(subject);
            //启用html
            helper.setText(content, true);
            return helper;
        } catch (MessagingException e) {
            throw new BusinessException("获取邮件助手失败");
        }
    }
}
