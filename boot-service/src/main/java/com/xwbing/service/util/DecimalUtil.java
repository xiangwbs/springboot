package com.xwbing.service.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import com.xwbing.service.exception.UtilException;

/**
 * 数字工具类
 *
 * @author xiangwb
 */
public class DecimalUtil {
    private static final int DEF_DIV_SCALE = 5;

    public static BigDecimal toYuan(Long l) {
        if (l == null) {
            return null;
        }
        return BigDecimal.valueOf(l).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_HALF_UP);
    }

    public static Long toFen(BigDecimal b) {
        if (b==null) {
            return null;
        }
        return b.multiply(BigDecimal.valueOf(100)).longValue();
    }

    /**
     * 两个Double数相加
     *
     * @param v1
     * @param v2
     *
     * @return Double
     */
    public static Double add(Double v1, Double v2) {
        BigDecimal b1 = BigDecimal.valueOf(v1);
        BigDecimal b2 = BigDecimal.valueOf(v2);
        return b1.add(b2).doubleValue();
    }

    /**
     * 两个Double数相减
     *
     * @param v1
     * @param v2
     *
     * @return Double
     */
    public static Double sub(Double v1, Double v2) {
        BigDecimal b1 = BigDecimal.valueOf(v1);
        BigDecimal b2 = BigDecimal.valueOf(v2);
        return b1.subtract(b2).doubleValue();
    }

    /**
     * 两个Double数相乘
     *
     * @param v1
     * @param v2
     *
     * @return Double
     */
    public static Double mul(Double v1, Double v2) {
        BigDecimal b1 = BigDecimal.valueOf(v1);
        BigDecimal b2 = BigDecimal.valueOf(v2);
        return b1.multiply(b2).doubleValue();
    }

    /**
     * 两个Double数相除
     *
     * @param v1
     * @param v2
     *
     * @return Double
     */
    public static Double div(Double v1, Double v2) {
        BigDecimal b1 = BigDecimal.valueOf(v1);
        BigDecimal b2 = BigDecimal.valueOf(v2);
        return b1.divide(b2, DEF_DIV_SCALE, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 两个Double数相除，并保留scale位小数
     *
     * @param v1
     * @param v2
     * @param scale
     *
     * @return Double
     */
    public static Double div(Double v1, Double v2, int scale) {
        if (scale < 0) {
            throw new UtilException("小数位数不能为负数");
        }
        BigDecimal b1 = BigDecimal.valueOf(v1);
        BigDecimal b2 = BigDecimal.valueOf(v2);
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 格式化double 四舍五入 保留n位小数
     *
     * @param d
     * @param scale 小数位
     *
     * @return
     */
    public static Double format(Double d, int scale) {
        BigDecimal bg = BigDecimal.valueOf(d);
        return bg.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 不四舍五入保留n位小数
     *
     * @param scale 小数位
     * @param d
     *
     * @return
     */
    public static double decimalND(Double d, int scale) {
        BigDecimal bg = BigDecimal.valueOf(d);
        return bg.setScale(scale, BigDecimal.ROUND_DOWN).doubleValue();
    }

    /**
     * 保留n位小数 对非零舍弃部分前面的数字加1
     *
     * @param scale 小数位
     * @param d
     *
     * @return
     */
    public static double decimalNU(Double d, int scale) {
        BigDecimal bg = BigDecimal.valueOf(d);
        return bg.setScale(scale, BigDecimal.ROUND_UP).doubleValue();
    }

    public static void main(String[] args) {
        BigDecimal a = BigDecimal.valueOf(1);
        BigDecimal b = BigDecimal.valueOf(2);
        //前提为a、b均不能为null
        if (a.compareTo(b) == -1) {
            System.out.println("a小于b");
        }

        if (a.compareTo(b) == 0) {
            System.out.println("a等于b");
        }

        if (a.compareTo(b) == 1) {
            System.out.println("a大于b");
        }

        if (a.compareTo(b) > -1) {
            System.out.println("a大于等于b");
        }

        if (a.compareTo(b) < 1) {
            System.out.println("a小于等于b");
        }

        DecimalFormat df1 = new DecimalFormat("0.0");
        DecimalFormat df2 = new DecimalFormat("#.#");
        DecimalFormat df3 = new DecimalFormat("000.000");
        DecimalFormat df4 = new DecimalFormat("###.###");
        System.out.println(df1.format(12.34));
        System.out.println(df2.format(12.34));
        System.out.println(df3.format(12.34));
        System.out.println(df4.format(12.34));
    }
}

