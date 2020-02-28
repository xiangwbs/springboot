package com.xwbing.util;

import com.xwbing.exception.UtilException;

import java.util.Random;

/**
 * 生成随机码
 *
 * @author xiangwb
 */
public class RadomUtil {
    public static String buildRandom(int length) {
        if (length < 1) {
            throw new UtilException("参数异常!!!");
        }
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
            int number = random.nextInt(str.length());
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        while (true)
            System.out.println(buildRandom(8));
    }
}
