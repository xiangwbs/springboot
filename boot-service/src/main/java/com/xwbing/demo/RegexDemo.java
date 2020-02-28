package com.xwbing.demo;

import java.util.Arrays;

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

        System.out.println("13488888888".matches("1[3578]\\d{9}"));
        System.out.println(Arrays.toString(("aa.bb").split("\\.")));
        System.out.println("xiangggweiiii".replaceAll("(.)\\1+","$1"));
    }
}
