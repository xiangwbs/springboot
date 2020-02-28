package com.xwbing.util;

import com.xwbing.exception.UtilException;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * ZipUtil
 *
 * @author xiangwb
 */
@Slf4j
public class ZipUtil {
    /**
     * @param response 响应
     * @param files    所有文件
     * @param fileName 文件名
     */
    public static void zipFile(HttpServletResponse response, List<File> files, String path, String fileName) {
        try {
            //classpath下有file文件夹
            File zipFile = new File(path + File.separator + fileName + ".zip");
            if (!zipFile.exists()) {
                zipFile.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(zipFile);
            ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(fos));
            FileInputStream fis;
            BufferedInputStream bis;
            byte[] data = new byte[1024 * 10];
            if (files != null && files.size() > 0) {
                for (File file : files) {
                    String name = file.getName();
                    //创建ZIP实体，并添加进压缩包
                    ZipEntry zipEntry = new ZipEntry(name);
                    zos.putNextEntry(zipEntry);
                    //读取待压缩的文件并写进压缩包里
                    fis = new FileInputStream(file);
                    bis = new BufferedInputStream(fis);
                    int d;
                    while ((d = bis.read(data, 0, 1024 * 10)) != -1) {
                        zos.write(data, 0, d);
                    }
                    bis.close();
                    fis.close();
                }
            }
            zos.close();
            // 输出到客户端
            OutputStream out = response.getOutputStream();
            response.reset();
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".zip");//在消息头里命名输出的zip文件夹名称
            response.setContentType("application/octet-stream; charset=utf-8");
            out.write(toByte(zipFile));
//            out.flush();
            out.close();
            zipFile.delete();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new UtilException("文件压缩错误");
        }
    }

    /**
     * 文件转换byte字节数组
     *
     * @param file
     * @return
     */
    private static byte[] toByte(File file) {
        {
            byte[] bytes;
            try {
                FileInputStream fis = new FileInputStream(file);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                byte[] data = new byte[fis.available()];
                int len;
                while ((len = fis.read(data)) != -1) {
                    bos.write(data, 0, len);
                }
                bytes = bos.toByteArray();
                fis.close();
                bos.close();
            } catch (IOException e) {
                log.error(e.getMessage());
                throw new UtilException("文件转化错误");
            }
            return bytes;
        }
    }
}

