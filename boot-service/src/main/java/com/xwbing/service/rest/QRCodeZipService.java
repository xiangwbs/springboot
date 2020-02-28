package com.xwbing.service.rest;

import com.xwbing.exception.BusinessException;
import com.xwbing.util.QRCodeUtil;
import com.xwbing.util.RestMessage;
import com.xwbing.util.ZipUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 创建时间: 2017/11/17 13:36
 * 作者: xiangwb
 * 说明:
 */
@Slf4j
@Service
public class QRCodeZipService {
    /**
     * 生成默认长度的二维码
     *
     * @param name 图片名
     * @param text 二维码内容
     * @return
     */
    public RestMessage createQRCode(String name, String text) {
        RestMessage result = new RestMessage();
        String path = getPath();
        File output = new File(path + File.separator + name + ".png");
        QRCodeUtil.createCode(text, output);
        result.setSuccess(true);
        result.setMessage("生成二维码成功");
        return result;
    }

    /**
     * 解析二维码
     *
     * @param file
     * @return
     */
    public RestMessage decode(File file) {
        RestMessage result = new RestMessage();
        try {
            String decode = QRCodeUtil.decode(file);
            result.setData(decode);
            result.setSuccess(true);
            result.setMessage("解析二维码成功");
            return result;
        } catch (Exception e) {
            log.error("解析二维码失败");
            throw new BusinessException("解析二维码失败");
        }
    }

    /**
     * 获取图片zip
     *
     * @return
     */
    public RestMessage batchGetImage(HttpServletResponse response, String[] names, String fileName) {
        List<File> files = new ArrayList<>();
        if (names.length == 0) {
            throw new BusinessException("请选择要导出的图片");
        }
        Arrays.stream(names).forEach(name -> {
            File file = getFile(name);
            if (file != null && file.length() > 0) {
                files.add(file);
            }
        });
        return zipFile(response, files, fileName);
    }

    /**
     * 根据文件名获取图片
     *
     * @param name
     * @return
     */
    private File getFile(String name) {
        String path = getPath();
        File output = new File(path + File.separator + name + ".png");
        if (output.exists()) {
            return output;
        }
        return null;
    }

    /**
     * 获取pic路径
     *
     * @return
     */
    private String getPath() {
        ClassPathResource pic = new ClassPathResource("pic");
        String absolutePath;
        try {
            absolutePath = pic.getFile().getAbsolutePath();
        } catch (IOException e) {
            throw new BusinessException("获取图片路径错误");
        }
        return absolutePath;
    }

    /**
     * 导出文件
     *
     * @return
     */
    private RestMessage zipFile(HttpServletResponse response, List<File> files, String fileName) {
        RestMessage restMessage = new RestMessage();
        String path = getPath();
        ZipUtil.zipFile(response, files, path, fileName);
        restMessage.setSuccess(true);
        restMessage.setMessage("导出zip文件成功");
        return restMessage;
    }
}
