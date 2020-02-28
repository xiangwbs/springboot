package com.xwbing.util;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串处理工具类
 */
public class StringUtil {
    /**
     * 去掉最后多余的0
     *
     * @param s
     * @return
     */
    public static String subZeroAndDot(String s) {
        if (s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", "");
            s = s.replaceAll("[.]$", "");
        }
        return s;
    }

    /**
     * 验证账号是否合法
     *
     * @param account
     * @return
     */
    public static boolean validateAccount(String account) {
        String REGEX = "[a-zA-Z0-9_]{4,16}$";
        Pattern pattern = Pattern.compile(REGEX);
        Matcher matcher = pattern.matcher(account);
        return matcher.matches();
    }

    /**
     * 字符encode
     *
     * @param content
     * @return
     */
    public static String encode(String content) {
        try {
            content = URLEncoder.encode(content, "utf-8");
            return content;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 根据身份证号获取年龄
     *
     * @param cardId
     * @return
     */
    public static Long getAgeByCardId(String cardId) {
        if (StringUtils.isEmpty(cardId)) {
            return -1L;
        }
        String dateStr;
        if (cardId.length() == 15) {
            dateStr = "19" + cardId.substring(6, 12);
        } else if (cardId.length() == 18) {
            dateStr = cardId.substring(6, 14);
        } else {//默认是合法身份证号，但不排除有意外发生
            return -1L;
        }
        LocalDate start = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyyMMdd"));
        LocalDate now = LocalDate.now();
        return ChronoUnit.YEARS.between(start, now);
    }

    /**
     * 去掉字符串中包含的特殊字符
     *
     * @param str
     * @return
     */
    public static String compileStr(String str) {
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\\\.<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    /**
     * 加密手机号
     *
     * @param mobile
     * @return
     */
    public static String encryptMobile(String mobile) {
        if (mobile.length() != 11) {
            return null;
        }
        return mobile.substring(0, 3) + "****" + mobile.substring(7, mobile.length());
    }
}
