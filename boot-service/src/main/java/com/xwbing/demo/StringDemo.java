package com.xwbing.demo;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Scanner;

/**
 * 创建日期: 2017年2月16日 下午5:27:23
 * 作者: xiangwb
 */

public class StringDemo {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("请输入:");
        String strr = sc.next();
        System.out.println(strr);

        /**
         * 基本语法
         */
        String str = "thinking in java";
        str.trim();// 去除当前字符两边的空格
        str.toUpperCase();// 转换为全大写
        str.toLowerCase();// 转换为全小写
        boolean contains = str.contains("ss");//判断是否包含某字符
        int index = str.indexOf("in");// 查找in在str字符串中的位置，若没有返回-1
        index = str.indexOf("in", 3);// 从指定位置处开始查找
        index = str.lastIndexOf("in");// 最后一次出现位置
        char c = str.charAt(5);// 获取当前字符串指定下标对应的位置
        boolean starts = str.startsWith("thi");// 判断当前字符串是否是以指定的字符串开始
        boolean ends = str.endsWith("ava");// 判断当前字符串是否是以指定的字符串结尾
        String sub = str.substring(5, 9);// 子集，含头不含尾
        sub = str.substring(1);// 指定位置到末尾
        System.out.println(sub);

        /**
         * 正则表达式：匹配
         */
        String regex = "[a-zA-Z0-9_]+@[a-zA-Z0-9_]+(\\.[a-z]+)+";
        String mail = "fancq@tedu.cn.cn";
        boolean flag = mail.matches(regex);
        if (flag) {
            System.out.println("是邮箱");
        } else {
            System.out.println("不是邮箱");
        }

        /**
         * 正则表达式：分割
         */
        String imagName = "1.jpg";
        String names[] = imagName.split("\\.");
        imagName = System.currentTimeMillis() + "." + names[1];
        System.out.println(imagName);

        /**
         * 正则表达式：替换
         */
        String imgName = "1.jpg";
        imgName = imgName.replaceAll(".+\\.", System.currentTimeMillis() + ".");
        System.out.println(imgName);

        /**
         * StringBuilder
         */
        String sstr = "不断学习";
        StringBuilder b = new StringBuilder(sstr);
        b.append("提升自己");
        System.out.println(b.toString());
        b.replace(5, 9, "就是为了改变世界");
        System.out.println(b.toString());
        b.delete(0, 5);
        System.out.println(b.toString());
        b.insert(0, "活着,");
        System.out.println(b.toString());

        /**
         * 判断日期字符串大小     >0 =0 <0
         */
        int n = "2018-08-08 08:08:08".compareTo("2018-08-08 08:08:09");
        System.out.println(n >= 0);

        /**
         * 格式化字符串
         */
        System.out.println(String.format("xwbing %s", "项伟兵"));
        System.out.println(String.format("xwbing %02d", 1));
        System.out.println(MessageFormat.format("xwbing {0}", "项伟兵"));
        //努力拉到需要的位数
        System.out.println(new DecimalFormat("#####.###").format(11.23));
        //不够补0
        System.out.println(new DecimalFormat("0000.000").format(11.23));
    }
}
