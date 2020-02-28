package com.xwbing.util.wxpay;

import java.util.Random;

/**
 * Created by drore-wzm on 2015/11/23.
 */
public class RandomKit {
    static String[] la_arrs = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "S", "X", "Y", "Z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

    /****
     * 生成len个长度的随机字符
     * @param len
     * @return
     */
    public static synchronized String buildRandom(int len) {
        if (len <= 0) {
            throw new RuntimeException("参数异常!!!");
        }
        StringBuffer random = new StringBuffer();
        for (int i = 0; i < len; i++) {
            random.append(la_arrs[new Random().nextInt(la_arrs.length)]);
        }
        return random.toString();
    }

    public static void main(String[] args) {
        System.out.println(buildRandom(32));
    }

}
