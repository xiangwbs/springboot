
package com.xwbing.util;

import com.xwbing.exception.UtilException;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import static java.util.UUID.randomUUID;

/**
 * 散列算法/摘要算法
 *
 * @author xiangwb
 */
@Slf4j
public class DigestsUtil {
    private static final String SHA1 = "SHA-1";
    private static final String MD5 = "MD5";

    /**
     * 对输入字符串进行sha1散列.
     */
    public static byte[] sha1(byte[] input) {
        return digest(input, SHA1, null, 1);
    }

    public static byte[] sha1(byte[] input, byte[] salt) {
        return digest(input, SHA1, salt, 1);
    }

    public static byte[] sha1(byte[] input, byte[] salt, int iterations) {
        return digest(input, SHA1, salt, iterations);
    }

    /**
     * 对字符串进行加密, 支持md5与sha1算法.
     */
    private static byte[] digest(byte[] input, String algorithm, byte[] salt, int iterations) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            if (salt != null) {
                digest.update(salt);
            }
            byte[] result = digest.digest(input);
            for (int i = 1; i < iterations; i++) {
                digest.reset();
                result = digest.digest(result);
            }
            return result;
        } catch (GeneralSecurityException e) {
            log.error(e.getMessage());
            throw new UtilException("加密失败");
        }
    }

    /**
     * 对文件进行md5摘要.
     */
    public static byte[] md5(InputStream input) {
        return digest(input, MD5);
    }

    /**
     * 对文件进行sha1散列.
     */
    public static byte[] sha1(InputStream input) {
        return digest(input, SHA1);
    }

    private static byte[] digest(InputStream input, String algorithm) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
            int bufferLength = 8 * 1024;
            byte[] buffer = new byte[bufferLength];
            int read = input.read(buffer, 0, bufferLength);
            while (read > -1) {
                messageDigest.update(buffer, 0, read);
                read = input.read(buffer, 0, bufferLength);
            }
            return messageDigest.digest();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new UtilException("加密失败");
        }
    }

    /**
     * 获取签名
     *
     * @return
     */
    public static String getSign() {
        String sign = String.valueOf(System.currentTimeMillis()) + randomUUID();
        try {
            MessageDigest md = MessageDigest.getInstance(MD5);
            byte[] md5 = md.digest(sign.getBytes());
            return Base64.getUrlEncoder().encodeToString(md5).replaceAll("[-_=]", "");
        } catch (NoSuchAlgorithmException e) {
            log.error(e.getMessage());
            throw new UtilException("获取签名失败");
        }
    }

    public static void main(String[] args) {
        System.out.println(getSign());
    }
}
