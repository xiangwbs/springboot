package com.xwbing.service.demo;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.PhoneUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;

/**
 * 项目名称: boot-module-pro
 * 创建时间: 2018/7/11 下午8:59
 * 作者: xiangwb
 * 说明: 正则表达式
 */
public class RegexDemo {
    public static void main(String[] args) {
        String s1 = "[abc]";//abc任意字符
        String s2 = "[^abc]";//除abc任意字符
        String s3 = "[a-zA-Z0-9]";//a~zA~Z0~9任意一个字符
        String s4 = ".";//任意字符
        String s5 = "\\d";//数字字符
        String s6 = "\\w";//单词字符[a-zA-Z0-9_]
        String s7 = "\\s";//空白字符
        String s8 = "\\D";//非数字字符
        String s9 = "\\W";//非单词字符
        String s10 = "\\S";//非空白字符

        String a1 = "x?";//0-1
        String a2 = "x*";//0-n
        String a3 = "x+";//1-n
        String a4 = "x{n}";//n
        String a5 = "x{n,}";//>=n
        String a6 = "x{n,m}";//n-m
        String a7 = "^$";//匹配边界
        String a8 = "\\b";//单词边界

        String a9 = "[\\u4e00-\\u9fa5]";//汉字

        boolean number = Validator.isNumber("12345");
        boolean word = Validator.isWord("单词");
        boolean chinese = Validator.isChinese("汉字");
        boolean url = Validator.isUrl("网址");
        boolean email = Validator.isEmail("xiangwb@163.com");
        boolean zipCode = Validator.isZipCode("邮编");
        boolean phone = PhoneUtil.isPhone("电话号码");
        boolean tel = PhoneUtil.isTel("座机号码");
        boolean citizenId = Validator.isCitizenId("身份证号码");
        boolean ipv4 = Validator.isIpv4("127.0.0.1");
        boolean ipv6 = Validator.isIpv6("127.0.0.1");
        boolean upperCase = Validator.isUpperCase("大写");

        System.out.println("13488888888".matches("^1[3456789]\\d{9}$"));
        // 用户名部分：由字母（大小写不限）、数字、下划线、点、百分号、加号或减号组成。
        // @ 符号：作为用户名部分和域名部分之间的分隔符。
        // 域名部分：由字母（大小写不限）、数字、点或减号组成。
        // 顶级域名：由两个或更多字母组成
        System.out.println("xiangwb@163.com".matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"));
        // 以"http://"、"https://"或"ftp://"开头的网址。假设网址中不包含空格、斜杠、问号或井号等，并且不以空格结尾
        System.out.println("https://www.baidu.com".matches("^(?:https?|ftp)://[^\\s/$.?#].[^\\s]*$"));
        // IPv4地址
        System.out.println("127.0.0.1".matches("(?:\\d{1,3}\\.){3}\\d{1,3}"));
        // 获取第一组数字字符
        System.out.println(ReUtil.getGroup0("\\d+", "qaz1235qwer5678"));
        // 获取所有数字字符
        System.out.println(ReUtil.findAllGroup0("\\d+", "qaz1235qwer5678"));
        // 获取第一组数字
        System.out.println(ReUtil.getGroup0("(\\d+\\.?\\d*)", "wed23sd2.4ssd"));
        // 获取${}里的字符
        System.out.println(StrUtil.subBetween(ReUtil.getGroup0("\\$\\{\\w+}", "123${code}456"), "${", "}"));
        System.out.println("xiangggweiiii".replaceAll("(.)\\1+", "$1"));
    }
}