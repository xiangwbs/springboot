package com.xwbing.domain.entity.model;

import lombok.Data;

import java.util.Date;

/**
 * 说明: 邮箱属性
 * 创建时间: 2017/5/10 16:36
 * 作者:  xiangwb
 */
@Data
public class EmailModel {
    public EmailModel() {
        //默认值
    }

    /**
     * 发送邮件协议名称，默认smtp
     */
    private String protocol;
    /**
     * 发送邮箱地址
     */
    private String fromEmail;
    /**
     * 邮箱密码
     */
    private String password;
    /**
     * 接收邮箱
     */
    private String toEmail;
    /**
     * 设置发送时间，默认是即时发送
     */
    private Date sendTime;
    /**
     * 发送邮件服务器主机
     */
    private String serverHost;
    /**
     * 发送邮件服务器端口
     */
    private Integer serverPort;
    /**
     * 是否需要身份验证
     */
    private boolean auth;

    /**
     * 邮件主题
     */
    private String subject;
    /**
     * 邮件内容
     */
    private String centent;
    /**
     * 邮件附件的文件名
     */
    private String[] attachFileNames;
}
