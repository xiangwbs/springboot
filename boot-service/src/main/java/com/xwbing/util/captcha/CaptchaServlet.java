package com.xwbing.util.captcha;

import com.xwbing.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 创建验证码的servlet
 *
 * @author xiangwb
 */
@Slf4j
@WebServlet(name = "captchaServlet", urlPatterns = {"/captcha"})
public class CaptchaServlet extends HttpServlet {
    private static final long serialVersionUID = -8687266469702749102L;

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res) {
        // 设置相应类型,告诉浏览器输出的内容为图片
        res.setContentType("image/jpeg");
        // 禁止图像缓存。
        res.setHeader("Pragma", "No-cache");
        res.setHeader("Cache-Control", "no-cache");
        res.setDateHeader("Expire", 0);
        try {
            CaptchaUtil tool = new CaptchaUtil();
            StringBuffer code = new StringBuffer();
            BufferedImage image = tool.genRandomCodeImage(code);
            HttpSession session = req.getSession();
            session.setAttribute(CommonConstant.KEY_CAPTCHA, code.toString());
//            CommonDataUtil.setData(CommonConstant.KEY_CAPTCHA, code.toString());
            // 将内存中的图片通过流形式输出到客户端
            OutputStream out = res.getOutputStream();
            ImageIO.write(image, "JPEG", out);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new RuntimeException("获取验证码错误");
        }
    }
}
