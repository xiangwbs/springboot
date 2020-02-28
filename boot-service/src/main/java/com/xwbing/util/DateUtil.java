package com.xwbing.util;

import com.xwbing.exception.BusinessException;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 日期处理类
 *
 * @author xiangwb
 */
public class DateUtil {
    private static DecimalFormat df = new DecimalFormat("######0.00");
    public static final long MILLIS = 1;
    public static final long SECOND = MILLIS * 1000;
    public static final long MINUTE = SECOND * 60;
    public static final long HOUR = MINUTE * 60;
    public static final long DAY = 24 * HOUR;
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String YYYY_MM = "yyyy-MM";
    public static final String YYYY = "yyyy";
    public static final String HHMMSS = "HH:mm:ss";
    public static final String HHMM = "HH:mm";
    public static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
    /**
     * SimpleDateFormat是线程不安全,创建SimpleDateFormat实例需要耗费很大的代价
     * 使用ThreadLocal将共享变量变为独享，线程独享肯定能比方法独享在并发环境中能减少不少创建对象的开销。如果对性能要求比较高的情况下，一般推荐使用这种方法。
     */
    private static Map<String, ThreadLocal<DateFormat>> sdfMap = new HashMap<>();

    private static DateFormat getDateFormat(String pattern) {
        ThreadLocal<DateFormat> threadLocal = sdfMap.get(pattern);
        if (threadLocal == null) {
            threadLocal = ThreadLocal.withInitial(() -> new SimpleDateFormat(pattern));
            sdfMap.put(pattern, threadLocal);
        }
        return threadLocal.get();
    }

    private static Date parse(String pattern, String dateStr) {
        try {
            return getDateFormat(pattern).parse(dateStr);
        } catch (ParseException e) {
            throw new BusinessException("时间格式转换错误!" + dateStr);
        }
    }

    private static String format(String pattern, Date date) {
        return getDateFormat(pattern).format(date);
    }
    // //////////////////////基本转换////////////////////////////////////////////////////////////////////////

    /**
     * data转string
     *
     * @param d
     * @param pattern
     * @return
     */
    public static String date2Str(Date d, String pattern) {
        return format(pattern, d);
    }

    /**
     * 日期string转date
     *
     * @param dateStr
     * @param pattern
     * @return
     */
    public static Date str2Date(String dateStr, String pattern) {
        return parse(pattern, dateStr);
    }

    public static void main(String[] args) {
        Date date = str2Date("2016-10", YYYY_MM);
        System.out.println(date);
    }

    /**
     * 将毫秒转换为时间字符串
     *
     * @param ms
     * @param pattern
     * @return
     */
    public static String msToDateStr(String ms, String pattern) {
        long lt = new Long(ms);
        Date date = new Date(lt);
        return date2Str(date, pattern);
    }

    /**
     * 将时间戳转换为时间字符串
     *
     * @param str
     * @param pattern
     * @return
     */
    public static String stampToDateStr(String str, String pattern) {
        long lt = new Long(str);
        Date date = new Date(lt * 1000);
        return date2Str(date, pattern);
    }

    /**
     * 将时间字符串转为毫秒字符串
     *
     * @param str
     * @param pattern
     * @return
     */
    public static String dateStrToMs(String str, String pattern) {
        return String.valueOf(parse(pattern, str).getTime());
    }

    /**
     * 将时间字符串转为时间戳
     *
     * @param str
     * @param pattern
     * @return
     */
    public static String dateStrToStamp(String str, String pattern) {
        return String.valueOf(parse(pattern, str).getTime() / 1000);

    }

    // ///////////////////////////获取数据////////////获取数据/////////////////////////////////////////////////////////////

    /**
     * 获取当前时间之后的多少分钟时间
     *
     * @param minute
     * @return
     */
    public static Date nowTimeAfterMinute(int minute) {
        long curren = System.currentTimeMillis();
        curren += minute * 60 * 1000;
        return new Date(curren);
    }

    /**
     * 获取n小时后的时间字符串 无跨天情况 time:09:00 h:1.5 小时
     *
     * @param time 格式 hh:mm
     * @param h
     * @return
     */
    public static String nHoursTime(String time, double h) {
        String date = "";
        String[] c = time.split(":");
        double oldHour = Double.valueOf(c[0]);
        double oldMin = Double.valueOf(c[1]);
        oldMin = oldMin / 60;
        double oldTime = oldHour + oldMin;
        oldTime = oldTime + h;
        if (oldTime < 10) {
            date = "0";
        }
        date = date + String.valueOf(oldTime);
        double newMin = Double.valueOf(date.substring(date.indexOf('.')));
        String newM = String.valueOf(newMin * 60).substring(0, 2);
        String newH = date.substring(0, date.indexOf('.'));
        date = newH + ":" + newM;
        return date;
    }

    /**
     * 获取前几天是周几
     *
     * @param day 0代表当天
     * @return
     */
    public static String getBeforWeek(int day) {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        date.setTime(date.getTime() - 1000 * 60 * 60 * 24 * day);
        calendar.setTime(date);
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        String[] data = {"日", "一", "二", "三", "四", "五", "六"};
        return "周" + data[week - 1];
    }

    /**
     * 计算两个时间段相差几小时（09:00-13:00）
     *
     * @param startTime 09:00
     * @param endTime   13:00
     */
    public static String calculationTime(String startTime, String endTime) {
        String startH = startTime.substring(0, startTime.indexOf(':'));
        String startM = startTime.substring(startTime.indexOf(':') + 1);
        double startHour = Double.valueOf(startH) + Double.valueOf(startM) / 60;
        String endH = endTime.substring(0, endTime.indexOf(':'));
        String endM = endTime.substring(endTime.indexOf(':') + 1);
        double endHour = Double.valueOf(endH) + Double.valueOf(endM) / 60;
        double between = endHour - startHour;
        String leng = String.valueOf(df.format(between));
        if ("00".equals(leng.substring(leng.indexOf('.') + 1, leng.length()))) {
            leng = leng.substring(0, leng.indexOf('.'));
        }
        return leng;
    }

    /**
     * 获取n小时后的时间
     *
     * @param date
     * @param h
     * @return
     */
    public static String backTimeHour(Date date, int h) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR, h);
        SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
        return sdf.format(calendar.getTimeInMillis());
    }

    /**
     * 获取两个时间差
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static Map<String, Integer> getDatePoorHour(Date startDate, Date endDate) {
        long diff = startDate.getTime() - endDate.getTime();
        long diffSeconds = diff / SECOND % 60;
        long diffMinutes = diff / MINUTE % 60;
        long diffHours = diff / HOUR % 24;
        long diffDays = diff / DAY;
        Map<String, Integer> map = new HashMap<>();
        map.put("days", (int) diffDays);
        map.put("hours", (int) diffHours);
        map.put("minutes", (int) diffMinutes);
        map.put("seconds", (int) diffSeconds);
        return map;
    }

    /**
     * 查询几天前的字符串日期
     *
     * @param d
     * @param day
     * @return
     */
    public static String dateStrBefore(String d, int day) {
        Date formatDay = parse(d, YYYY_MM_DD);
        long dayBefore = formatDay.getTime() - day * 24 * 60 * 60 * 1000L;
        return date2Str(new Date(dayBefore), YYYY_MM_DD);
    }

    /**
     * 当前时间加n天
     *
     * @param date
     * @param day
     * @return
     */
    public static String backTimeDay(Date date, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, day);
        SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
        return sdf.format(calendar.getTimeInMillis());
    }

    // /////////////////////////////////////////////

    /**
     * 获取当月的第一天
     *
     * @return
     */
    public static String firstDayOfMonth() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH, 1);// 设置为1号,当前日期既为本月第一天
        return date2Str(c.getTime(), YYYY_MM_DD);
    }

    /**
     * 获取当年的第一天
     *
     * @return
     */
    public static String firstDayOfYear() {
        Calendar currCal = Calendar.getInstance();
        int currentYear = currCal.get(Calendar.YEAR);
        return date2Str(getYearFirst(currentYear), YYYY_MM_DD);
    }

    /**
     * 获取某年第一天日期
     *
     * @param year 年份
     * @return Date
     */
    public static Date getYearFirst(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        return calendar.getTime();
    }

    /**
     * 获取上个月第一天 YYYY-MM-dd
     *
     * @return
     */
    public static String getLastMonthFirst() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        String lastMonth = date2Str(cal.getTime(), YYYY_MM);
        return lastMonth + "-01";
    }

    /**
     * 获取上个月最后一天
     *
     * @return
     */
    public static String getLastMonthEnd() {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date strDateTo = calendar.getTime();
        return date2Str(strDateTo, YYYY_MM_DD);
    }

    /**
     * 获取指定月份最后一天
     *
     * @param monthStr YYYY-MM
     * @return
     */
    public static String getMonthEnd(String monthStr) {
        Date date = str2Date(monthStr, YYYY_MM);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
        calendar.roll(Calendar.DATE, -1);
        return date2Str(calendar.getTime(), YYYY_MM_DD);
    }

    /**
     * 获取对应年份每一个月份
     *
     * @param startMoth YYYY-MM
     * @param endMonth  YYYY-MM
     * @return
     */
    public static List<String> getYearMonth(String startMoth, String endMonth) throws ParseException {
        Date d1 = str2Date(startMoth, YYYY_MM);// 定义起始日期
        Date d2 = str2Date(endMonth, YYYY_MM);// 定义结束日期
        Calendar dd = Calendar.getInstance();// 定义日期实例
        dd.setTime(d1);// 设置日期起始时间
        List<String> list = new ArrayList<>();
        if (startMoth.equals(endMonth)) {
            list.add(startMoth);
            return list;
        }
        while (dd.getTime().before(d2)) {// 判断是否到结束日期
            list.add(date2Str(dd.getTime(), YYYY_MM));
            dd.add(Calendar.MONTH, 1);// 进行当前日期月份加1
        }
        list.add(endMonth);
        return list;

    }

    /**
     * 获取n年度
     *
     * @return
     */
    public static String getYear(int coun) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.YEAR, coun);
        Date y = c.getTime();
        return date2Str(y, YYYY);
    }

    // /////////////////
    // //////////////////////////////比较 排序///////////////比较
    // 排序/////////////////////////////////////////////

    /**
     * 时间字符串大小比较
     *
     * @param str1
     * @param str2
     * @return
     */
    public static boolean compareDate(String str1, String str2) {
        long longstr1 = Long.valueOf(str1.replaceAll("[-\\s:]", ""));
        long longstr2 = Long.valueOf(str2.replaceAll("[-\\s:]", ""));
        if (longstr1 >= longstr2) {
            return true;
        }
        return false;
    }

    /**
     * 判断两者时间是否重合 重合返回true
     *
     * @param startTime 占用的时间段 09:00
     * @param endTime   占用的时间段 13:00
     * @param needSTime 需要的时间段 09:00
     * @param needETime 需要的时间段 13:00
     * @return
     */
    public static boolean compare(String startTime, String endTime,
                                  String needSTime, String needETime) {
        if (needSTime.compareTo(endTime) == 0
                || needETime.compareTo(startTime) == 0) {// 这个表示开始时间等于结束时间,或者结束时间等于开始时间
            return false;
        }
        if (needSTime.compareTo(startTime) >= 0
                && needSTime.compareTo(endTime) < 0) {// 需要时间开始时间在比较时间之间，表示已经重复了
            return true;
        }
        if (needETime.compareTo(startTime) > 0
                && needETime.compareTo(endTime) <= 0) {// 需要时间结束时间在比较时间之间，表示已经重复了
            return true;
        }
        if (needSTime.compareTo(startTime) < 0
                && needETime.compareTo(endTime) > 0) {// 需要时间在比较时间前后，表示已经重复了
            return true;
        }
        return false;
    }

    /**
     * 比较两个日期相差的天数
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int daysBetween(String date1, String date2) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(str2Date(date1, YYYY_MM_DD));
        int day1 = calendar.get(Calendar.DAY_OF_YEAR);
        int year1 = calendar.get(Calendar.YEAR);
        calendar.setTime(str2Date(date2, YYYY_MM_DD));
        int day2 = calendar.get(Calendar.DAY_OF_YEAR);
        int year2 = calendar.get(Calendar.YEAR);
        int days = Math.abs(day1 - day2);// 天数绝对值
        if (year1 != year2) { // 不同年
            int years = 0;
            int maxYear = year1 > year2 ? year1 : year2;
            int minYear = year1 < year2 ? year1 : year2;
            for (int i = minYear; i < maxYear; i++) {
                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) { // 闰年
                    years += 366;
                } else { // 不是闰年
                    years += 365;
                }
            }
            return years + days;
        } else {// 同年
            return days;
        }
    }

    /**
     * 计算两个日期相差年数
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static int yearDateDiff(String startDate, String endDate) {
        Calendar calBegin = Calendar.getInstance(); // 获取日历实例
        Calendar calEnd = Calendar.getInstance();
        calBegin.setTime(str2Date(startDate, YYYY)); // 字符串按照指定格式转化为日期
        calEnd.setTime(str2Date(endDate, YYYY));
        return calEnd.get(Calendar.YEAR) - calBegin.get(Calendar.YEAR);
    }

    /**
     * 字符串类型日期集合排序
     *
     * @param list
     * @return
     * @throws ParseException
     */
    public static List<String> shortListDate(List<String> list)
            throws ParseException {
        List<Date> dateList = new ArrayList<>();
        List<String> dateStrList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            String Str = list.get(i);
            dateList.add(str2Date(Str, YYYY_MM_DD));
        }
        Comparator<Date> c = (begin, end) -> {
            if (begin.after(end)) {
                return 1;
            } else {
                return -1;
            }
        };
        dateList.sort(c);
        for (Date d : dateList) {
            dateStrList.add(date2Str(d, YYYY_MM_DD));
        }
        return dateStrList;
    }

    /**
     * 遍历获取两个日期之间天数
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static List<Date> listDateSplit(Date startDate, Date endDate) {
        if (!startDate.before(endDate))
            throw new BusinessException("开始时间应该在结束时间之后");
        Long spi = endDate.getTime() - startDate.getTime();
        Long step = spi / (24 * 60 * 60 * 1000);// 相隔天数
        List<Date> dateList = new ArrayList<>();
        dateList.add(endDate);
        for (int i = 1; i <= step; i++) {
            dateList.add(new Date(dateList.get(i - 1).getTime()
                    - (24 * 60 * 60 * 1000)));// 比上一天减一
        }
        Collections.reverse(dateList);
        return dateList;
    }

    /**
     * listDateToStr转换成
     *
     * @param list
     * @param pattern
     * @return
     */
    public static List<String> listDateToStr(List<Date> list, String pattern) {
        List<String> reList = new ArrayList<>();
        for (Date n : list) {
            reList.add(date2Str(n, pattern));
        }
        return reList;
    }
}