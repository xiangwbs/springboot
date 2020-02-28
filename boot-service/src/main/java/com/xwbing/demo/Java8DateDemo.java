package com.xwbing.demo;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.MonthDay;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 说明:
 * 创建日期: 2017年3月29日 下午4:37:09
 * 作者: xiangwb
 */

public class Java8DateDemo {
    public static void main(String[] args) {
        DateTimeFormatter formatted = DateTimeFormatter.ofPattern("YYYY-MM-dd");
        LocalDate date = LocalDate.now();// Current Date
        String dstr = date.format(formatted);// 格式化
        date = LocalDate.parse(dstr, formatted);// 解析
        LocalDate firstDay2017 = LocalDate.of(2017, 1, 1);// 2017-01-01
        LocalDate hundredDay2017 = LocalDate.ofYearDay(2017, 100);// 2017-03-29
        System.out.println(firstDay2017.equals(hundredDay2017));// 比较时间是否相等
        LocalDate todayKolkata = LocalDate.now(ZoneId.of("Asia/Kolkata"));
        int year = date.getYear();// 获取年
        int month = date.getMonthValue(); // 获取月
        int day = date.getDayOfMonth(); // 获取日
        date.isLeapYear();// 是否为闰年
        LocalDate dateOfBirth = LocalDate.of(2010, 05, 11);
        MonthDay monthDay = MonthDay.now();
        MonthDay birthday = MonthDay.of(dateOfBirth.getMonth(), dateOfBirth.getDayOfMonth());// 月日
        YearMonth yearMonth = YearMonth.of(2018, 8);// 年月
        Year yearr = Year.now();
        LocalDate nextYear = date.plusYears(1); // 1年后日期，可以一个月，一年，一小时，一分钟
        LocalDate beforYear = date.minusYears(1);// 1年前日期，可以一个月，一年，一小时，一分钟
        System.out.println(nextYear.isBefore(beforYear)); // 判断某个日期是在另一个日期的前面还是后面

        LocalTime time = LocalTime.now();// Current Time
        LocalTime specificTime = LocalTime.of(12, 0, 0);// 12:20:25
        LocalTime newSpecificTime = specificTime.plusHours(2);// 增加2小时
        LocalTime timeKolkata = LocalTime.now(ZoneId.of("Asia/Kolkata"));
        LocalTime specificSecondTime = LocalTime.ofSecondOfDay(1);// 00:00:01

        LocalDateTime dateTime = date.atTime(time);// Create LocalDateTime from
        // LocalDate
        LocalDateTime today = LocalDateTime.now();// Current Date
        today = LocalDateTime.of(LocalDate.now(), LocalTime.now());
        LocalDateTime specificDate = LocalDateTime.of(2014, Month.JANUARY, 1,
                10, 10, 30);// 2014-01-01T10:10:30
        LocalDateTime todayKolkata1 = LocalDateTime.now(ZoneId
                .of("Asia/Kolkata"));
        int second = today.getSecond();//获取时间中的秒
        int week = today.getDayOfWeek().getValue();//获取周几

        /*
         * 时钟
         */
        Clock clock = Clock.systemUTC();
        System.out.println(clock.millis());// 当前毫秒数
        Instant in = clock.instant();
        LocalDate d = LocalDate.now(clock);
        Instant timestamp = Instant.now();// 类似于java.util.Date


        /*
         *新旧日期转换
         */
        Instant instant = new Date().toInstant();
        Date dateee = Date.from(instant);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

        /*
         * 时间相差
         */
        LocalDateTime be = LocalDateTime.of(2016, 2, 11, 10, 30);
        LocalDateTime af = LocalDateTime.of(2016, 3, 11, 10, 30);
        Duration duration = Duration.between(be, af);
        System.out.println(duration.toDays() + duration.toHours());
    }
}