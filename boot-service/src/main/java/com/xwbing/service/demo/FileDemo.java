package com.xwbing.service.demo;

import cn.hutool.core.codec.Base64Encoder;
import cn.hutool.core.img.Img;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

/**
 * 说明: java.io.File File的每一个实例可以表示文件系统中的一个文件或目录 使用file可以：
 * 1：访问文件或目录的属性（如：大小，名字，修改时间等） 2；操作文件或目录（创建，删除文件和目录） 3：访问目录中的所有内容 但是不可以 访问文件数据
 * 创建日期: 2017年2月17日 下午2:26:18
 * 作者: xiangwb
 */

public class FileDemo {
    public static void main(String[] args) throws Exception {
        byte[] processedImageBytes;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Img.from(FileUtil.file("/Users/xwbing/Downloads/1245.png"))
                    .setQuality(0.8)//压缩比率
                    .write(FileUtil.file("/Users/xwbing/Downloads/1245632.jpg"));
            processedImageBytes = out.toByteArray();
            System.out.println("");
//            zjxfUploadBase64Byte(processedImageBytes);
        } catch (Exception e) {
        }


        Path basedir = FileSystems.getDefault().getPath("/tmp");//文件夹必须存在存在，否则报错
        Path tempFileNamePath = Files.createTempFile(basedir, "boot", ".txt");//创建随机文件名

        Path tmpDirectory = Files.createTempDirectory("tmp");//创建临时随机目录

        //创建文件对象
        File file = new File("/tmp" + File.separator + "file.txt");//创建文件对象，并不创建文件
        if (!file.exists()) {
            boolean newFile = file.createNewFile();//创建文件
        }
        if (file.exists()) {
            boolean delete = file.delete();//删除文件
        }
        Path path = FileSystems.getDefault().getPath("/tmp" + File.separator, "path.txt");
        String pathStr = path.toString();
        File pathFile = path.toFile();
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
        if (Files.exists(path)) {
            Files.delete(path);
        }

        String name = file.getName();//获取文件名
        long length = file.length();//获取文件大小
        long time = file.lastModified();//获取文件最后修改时间
        //查看文件是否具有可运行，可读，可写的权限
        boolean b = file.canExecute();
        boolean b1 = file.canRead();
        boolean b2 = file.canWrite();
        boolean isDir = file.isDirectory();//是否为一个目录
        boolean isFile = file.isFile();//是否为一个文件
        boolean isHidden = file.isHidden();//是否为一个隐藏文件

        /**
         * 目录
         */
        File dir = new File("demo");
        File[] subs = dir.listFiles();//获取目录下的所有子项
        if (!dir.exists()) {
            dir.mkdir();
        } else {
            dir.delete();//空目录才能删除
        }
        File dirs = new File("a" + File.separator + "b" + File.separator + "c");
        if (!dirs.exists()) {
            /*
             * 在创建当前目录的同时将不存在的父目录一并创建出来
             */
            dirs.mkdirs();
        }
        /**
         * 获取路径
         * java resources 编译后在WEB-INF/classes下 同级
         */
        String tomcatHome = System.getProperty("catalina.home");//服务器tomcat路径
        ClassPathResource pic = new ClassPathResource("pic");//获取classes下file文件夹路径
        String absolutePath = pic.getFile().getAbsolutePath();
        //配置文件
        String filePath = IODemo.class.getClassLoader().getResource("/redis.properties").getPath();
        InputStream in = IODemo.class.getResourceAsStream("/data/zjPublishJdZb.json");
        String treeStr = new String(IoUtil.readBytes(in), StandardCharsets.UTF_8);
    }


    public String zjxfAddFile(String url) {
        File tmpFile;
        try {
            String suffix = url.substring(url.lastIndexOf("."));
            tmpFile = File.createTempFile("zjxf119pic", suffix);
            HttpUtil.downloadFile(url, tmpFile);
            HashMap<String, Object> paramMap = new HashMap<>();
            paramMap.put("imgsfile", tmpFile);
            String result = HttpUtil.post("", paramMap);
            if (tmpFile.exists()) {
                tmpFile.delete();
            }
            JSONObject resultObj = JSONUtil.parseObj(result);
            Boolean success = resultObj.getBool("success", false);
            if (!success) {
                return null;
            }
            return resultObj.getStr("data");
        } catch (Exception e) {
            return null;
        }
    }

    public static JSONObject zjxfUploadBase64Byte(byte[] imageBytes, String authorization) {
        try {
            HashMap<String, Object> paramMap = new HashMap<>();
            byte[] processedImageBytes = imageBytes;
            if (imageBytes.length > 1024 * 1024) {
                // 压缩
                try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                    Thumbnails.of(IoUtil.toStream(imageBytes))
                            .scale(1.0)
                            .outputQuality(0.6)
//                            .outputFormat(suffix)
                            .toOutputStream(out);
                    processedImageBytes = out.toByteArray();
                    if (processedImageBytes.length >= imageBytes.length) {
                        processedImageBytes = imageBytes;
                    }
                } catch (Exception e) {
                    processedImageBytes = imageBytes;
                }
            }
            paramMap.put("base64Code", "data:image/jpeg;base64," + Base64Encoder.encode(processedImageBytes));
            String result = HttpRequest
                    .post("https://xfzzgl.zjxf119.com/v1/xyxf/member/file/zzglUploadBase64")
                    .form(paramMap)
                    .header("Authorization", authorization)
                    .execute()
                    .body();
            return JSONUtil.parseObj(result);
        } catch (Exception e) {
            return null;
        }
    }
}
