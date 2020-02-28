package com.xwbing.demo;

import java.util.Calendar;
import java.util.Date;

/**
 * 创建日期: 2017年2月16日 下午3:32:04
 * 作者: xiangwb
 */

public class CalendarDemo {
    public static void main(String[] args) {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        calendar.setTime(date);



		/*
		 * 设置calendar，使其表示 2008-08-08 08：08：08
		 */
        calendar.set(Calendar.YEAR, 2008);// 年
        calendar.set(Calendar.MONTH, 7);// 月从0开始，0表示1月
        /*
         * 设置日 对应的时间分量常用的： DATE：月中天 DAY_OF-MONTH:月中的天，与DATE一致
         * DAY_OF_WEEK:周中的天，星期几 DAY_OF_YEAR:年中的天
         */
        calendar.set(Calendar.DAY_OF_MONTH, 8);// 日
        calendar.set(Calendar.HOUR_OF_DAY, 8);// 时
        calendar.set(Calendar.MINUTE, 8);// 分
        calendar.set(Calendar.SECOND, 8);// 秒
        System.out.println(calendar.getTime());
        /*
         * int get(int field) 获取对应的分量的值
         */
        Calendar now = Calendar.getInstance();
        int year = now.get(Calendar.YEAR);// 获取年
        int month = now.get(Calendar.MONTH) + 1;// 获取月+1
        int day = now.get(Calendar.DATE);// 获取日
        int house = now.get(Calendar.HOUR);// 时
        int minute = now.get(Calendar.MINUTE);// 分
        int second = now.get(Calendar.SECOND);// 秒
        System.out.println(year + "-" + month + "-" + day + "   " + house + ":" + minute + ":" + second);


        // 查看星期几
        int week = now.get(Calendar.DAY_OF_WEEK);
        System.out.println("周" + (week == 1 ? 7 : week - 1));
        // 当年过了几天
        int doy = now.get(Calendar.DAY_OF_YEAR);
        System.out.println(year + "年已经过了" + doy + "天");
        // 当月共有多少天
        int days = now.getActualMaximum(Calendar.DAY_OF_MONTH);
        System.out.println("本月共有" + days + "天");
        // 当年共有多少天
        int years = now.getActualMaximum(Calendar.DAY_OF_YEAR);
        System.out.println("今年共有" + years + "天");
        // 查看今年2月份有几天
        now.set(Calendar.MONTH, 1);
        System.out.println(now.getActualMaximum(Calendar.DAY_OF_MONTH));




        /*
         * 查看3年2个月零8天以后的日期？
         */
        Calendar nowTime = Calendar.getInstance();
        System.out.println(nowTime.getTime());
        nowTime.add(Calendar.YEAR, 3);
        nowTime.add(Calendar.MONTH, 2);
        nowTime.add(Calendar.DAY_OF_YEAR, 8);// 用DAY_OF_YEAR
        System.out.println(nowTime.getTime());
        /*
         * 当遇到类似开发需求： 要求用户输入一个日期，然后对该日期进行一系列的运算 再将计算后的日期显示给用户时。 流程如下：
         * 1：获取用户输入的日期字符串
         * 2：使用SimpleDateFormat将其转换为Date
         * 3：创建一个Calendar。使其表示Date表示的日期
         * 4：使用Calendar根据需求计算时间
         * 5：将Calendar转换为一个Date 6：使用SimpleDateFormal将Date转换为字符串后显示给用户
         */
    }
}
