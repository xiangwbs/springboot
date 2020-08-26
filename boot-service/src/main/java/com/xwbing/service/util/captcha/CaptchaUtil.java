package com.xwbing.service.util.captcha;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

/**
 * 验证码工具类
 *
 * @author xiangwb
 */
public class CaptchaUtil {
    // 随机产生的字符串
    private static final String RANDOM_STRS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String FONT_NAME = "Fixedsys";
    private Random random = new Random();
    private static final int FONT_SIZE = 18;// 字体大小
    private static final int WIDTH = 80;// 图片宽
    private static final int HEIGHT = 25;// 图片高
    private static final int LINE_NUM = 50;// 干扰线数量
    private static final int STR_NUM = 4;// 随机产生字符数量

    /**
     * 生成随机图片
     */
    public BufferedImage genRandomCodeImage(StringBuffer randomCode) {
        // BufferedImage类是具有缓冲区的Image类
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT,
                BufferedImage.TYPE_INT_BGR);
        // 获取Graphics对象,便于对图像进行各种绘制操作
        Graphics g = image.getGraphics();
        // 设置背景色
        g.setColor(getRandColor(200, 250));
        // 填充矩形面积
        g.fillRect(0, 0, WIDTH, HEIGHT);
        // 设置干扰线的颜色
        g.setColor(getRandColor(110, 120));
        // 绘制干扰线
        for (int i = 0; i <= LINE_NUM; i++) {
            drowLine(g);
        }
        // 绘制随机字符
        g.setFont(new Font(FONT_NAME, Font.PLAIN, FONT_SIZE));
        for (int i = 1; i <= STR_NUM; i++) {
            randomCode.append(drowString(g, i));
        }
        g.dispose();
        return image;
    }

    /**
     * 给定范围获得随机颜色
     */
    private Color getRandColor(int fc, int bc) {
        if (fc > 255) {
            fc = 255;
        }
        if (bc > 255) {
            bc = 255;
        }
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }

    /**
     * 绘制字符串
     */
    private String drowString(Graphics g, int i) {
        g.setColor(new Color(random.nextInt(101), random.nextInt(111), random
                .nextInt(121)));
        String rand = String.valueOf(getRandomString(random.nextInt(RANDOM_STRS
                .length())));
        g.translate(random.nextInt(3), random.nextInt(3));
        g.drawString(rand, 13 * i, 16);
        return rand;
    }

    /**
     * 绘制干扰线
     */
    private void drowLine(Graphics g) {
        int x = random.nextInt(WIDTH);
        int y = random.nextInt(HEIGHT);
        int x0 = random.nextInt(16);
        int y0 = random.nextInt(16);
        g.drawLine(x, y, x + x0, y + y0);
    }

    /**
     * 获取随机的字符
     */
    private String getRandomString(int num) {
        return String.valueOf(RANDOM_STRS.charAt(num));
    }

    public static void main(String[] args) {
        CaptchaUtil tool = new CaptchaUtil();
        StringBuffer code = new StringBuffer();
        BufferedImage image = tool.genRandomCodeImage(code);
        System.out.println("random code : " + code);
        try {
            // 将内存中的图片通过流动形式输出到客户端
            ImageIO.write(image, "JPEG", new FileOutputStream(new File(
                    "random-code.jpg")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
