package com.xwbing.util;

import org.apache.commons.lang3.StringUtils;

/**
 * @author xiangwb
 */
public class EmojiUtil {
    /**
     * 清除特殊字符
     *
     * @param s
     * @return
     */
    public static String cleanSpecialStr(String s) {
        if (StringUtils.isNotEmpty(s)) {
            if (s.contains("'")) {
                s = s.replaceAll("'", "");
            }
            s = EmojiUtil.cleanEmoji(s);
        }
        return s;
    }

    /**
     * 清除一个字符串中的emoji表情字符
     *
     * @param s
     * @return
     */
    private static String cleanEmoji(String s) {
        if (StringUtils.isEmpty(s)) {
            return null;
        }
        StringBuilder builder = new StringBuilder(s);
        for (int i = 0; i < builder.length(); i++) {
            if (isEmoji(builder.charAt(i))) {
                builder.deleteCharAt(i);
                builder.insert(i, ' ');// 比字符串中直接替换字符要好，那样会产生很多字符串对象
            }
        }
        return builder.toString().trim();
    }

    /**
     * 判断一个字符是否emoji表情字符
     *
     * @param ch
     * @return
     */
    private static boolean isEmoji(char ch) {
        return !(ch == 0x0 || ch == 0x9 || ch == 0xA || ch == 0xD || ch >= 0x20 && ch <= 0xD7FF || ch >= 0xE000 && ch <= 0xFFFD);
    }
}