package com.xwbing.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.xwbing.exception.UtilException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author daofeng
 * @version $Id$
 * @since 2020年04月12日 下午4:43
 */
@Slf4j
public class FileUtil {
    public static byte[] toByte(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            return toByte(fis);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new UtilException("文件转为byte错误");
        }
    }

    public static byte[] toByte(InputStream inputStream) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] data = new byte[inputStream.available()];
            int len;
            while ((len = inputStream.read(data)) != -1) {
                bos.write(data, 0, len);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new UtilException("流转化为byte错误");
        }
    }

    public static byte[] toByte(String picUrl) {
        HttpURLConnection conn = null;
        InputStream inStream = null;
        try {
            //new一个URL对象
            URL url = new URL(picUrl);
            //打开链接
            conn = (HttpURLConnection)url.openConnection();
            //设置请求方式为"GET"
            conn.setRequestMethod("GET");
            //超时响应时间为5秒
            conn.setConnectTimeout(5 * 1000);
            //通过输入流获取图片数据
            inStream = conn.getInputStream();
            return toByte(inStream);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new UtilException("图片url转化为byte错误");
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
    }
}

