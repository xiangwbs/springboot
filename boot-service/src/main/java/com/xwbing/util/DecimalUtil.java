package com.xwbing.util;

import com.xwbing.exception.UtilException;

import java.math.BigDecimal;

/**
 * 数字工具类
 *
 * @author xiangwb
 */
public class DecimalUtil {
    private static final int DEF_DIV_SCALE = 5;

    /**
     * 两个Double数相加
     *
     * @param v1
     * @param v2
     * @return Double
     */
    public static Double add(Double v1, Double v2) {
        BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        return b1.add(b2).doubleValue();
    }

    /**
     * 两个Double数相减
     *
     * @param v1
     * @param v2
     * @return Double
     */
    public static Double sub(Double v1, Double v2) {
        BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        return b1.subtract(b2).doubleValue();
    }

    /**
     * 两个Double数相乘
     *
     * @param v1
     * @param v2
     * @return Double
     */
    public static Double mul(Double v1, Double v2) {
        BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        return b1.multiply(b2).doubleValue();
    }

    /**
     * 两个Double数相除
     *
     * @param v1
     * @param v2
     * @return Double
     */
    public static Double div(Double v1, Double v2) {
        BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        return b1.divide(b2, DEF_DIV_SCALE, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 两个Double数相除，并保留scale位小数
     *
     * @param v1
     * @param v2
     * @param scale
     * @return Double
     */
    public static Double div(Double v1, Double v2, int scale) {
        if (scale < 0) {
            throw new UtilException("小数位数不能为负数");
        }
        BigDecimal b1 = new BigDecimal(v1.toString());
        BigDecimal b2 = new BigDecimal(v2.toString());
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 格式化double 四舍五入 保留n位小数
     *
     * @param v1
     * @param scale 小数位
     * @return
     */
    public static Double format(Double v1, int scale) {
        BigDecimal bg = new BigDecimal(v1);
        return bg.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 不四舍五入保留n位小数
     *
     * @param scale 小数位
     * @param d
     * @return
     */
    public static double decimalND(Double d, int scale) {
        BigDecimal bg = new BigDecimal(d);
        return bg.setScale(scale, BigDecimal.ROUND_DOWN).doubleValue();
    }

    /**
     * 保留n位小数 对非零舍弃部分前面的数字加1
     *
     * @param scale 小数位
     * @param d
     * @return
     */
    public static double decimalNU(Double d, int scale) {
        BigDecimal bg = new BigDecimal(d);
        return bg.setScale(scale, BigDecimal.ROUND_UP).doubleValue();
    }

    public static void main(String[] args) {
        Double div = div((9.0), 8.0, 2);
        System.out.println(div);
        div = format(div, 1);
        System.out.println(div);
        Double format = format(6.2, 1);
        System.out.println(format);
        Double d = 1.11;
        int i = d.intValue();
        System.out.println(i);
    }
}

