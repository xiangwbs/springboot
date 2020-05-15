package com.xwbing.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import com.xwbing.exception.UtilException;

import lombok.extern.slf4j.Slf4j;

/**
 * ZipUtil
 *
 * @author xiangwb
 */
@Slf4j
public class ZipUtil {
    /**
     * @param response 响应
     * @param files 所有文件
     * @param fileName 文件名
     */
    public static void downloadZip(HttpServletResponse response, List<File> files, String path, String fileName) {
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
            out.write(FileUtil.toByte(zipFile));
            //            out.flush();
            out.close();
            zipFile.delete();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new UtilException("文件压缩错误");
        }
    }

    public void dealInputStream(InputStream inputStream, Charset charset) throws IOException {
        ZipInputStream zin = new ZipInputStream(inputStream, charset);
        BufferedInputStream bs = new BufferedInputStream(zin);
        ZipEntry ze;
        //循环读取压缩包里面的文件
        while ((ze = zin.getNextEntry()) != null) {
            if (ze.toString().endsWith("xxx.csv")) {
                byte[] bytes = new byte[(int)ze.getSize()];
                bs.read(bytes, 0, (int)ze.getSize());
                //将文件转成流
                InputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
                //处理文件
            }
        }
        zin.closeEntry();
        inputStream.close();
    }
}

