package com.xwbing.service.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import com.xwbing.service.exception.UtilException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author daofeng
 * @version $Id$
 * @since 2020年04月12日 下午4:43
 */
@Slf4j
public class FileUtil {
    public static InputStream urlToStream(String url) {
        try (InputStream inputStream = new URL(url).openConnection().getInputStream()) {
            return inputStream;
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new UtilException("url转化为流错误");
        }
    }

    public static byte[] urlToByte(String url) {
        try (InputStream inputStream = new URL(url).openConnection().getInputStream()) {
            return toByte(inputStream);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new UtilException("url转化为byte错误");
        }
    }

    public static File urlToFile(String url, String fullPath) {
        try (InputStream inputStream = new URL(url).openConnection().getInputStream()) {
            return toFile(inputStream, fullPath);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new UtilException("图片url转化为byte错误");
        }
    }

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
            byte[] data = new byte[1024 * 2];
            int index;
            while ((index = inputStream.read(data)) != -1) {
                bos.write(data, 0, index);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new UtilException("流转化为byte错误");
        }
    }

    public static File toFile(InputStream inputStream, String fullPath) throws IOException {
        Path path = FileSystems.getDefault().getPath(fullPath);
        if (Files.exists(path)) {
            Files.delete(path);
        }
        try (FileOutputStream output = new FileOutputStream(Files.createFile(path).toFile())) {
            byte[] data = new byte[1024 * 2];
            int index;
            while ((index = inputStream.read(data)) != -1) {
                output.write(data, 0, index);
            }
            return path.toFile();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new UtilException("流转化为文件错误");
        }
    }
}

