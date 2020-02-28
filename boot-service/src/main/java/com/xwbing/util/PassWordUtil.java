package com.xwbing.util;

import com.xwbing.exception.UtilException;
import org.apache.commons.lang3.StringUtils;

import java.security.SecureRandom;
import java.util.UUID;

/**
 * 密码工具类
 *
 * @author xiangwb
 */
public class PassWordUtil {
    /**
     * 初始默认密码长度
     */
    public static final int PWD_LENGTH = 8;
    /**
     * 密码盐长度
     */
    public static final int SALT_SIZE = 10;
    /**
     * hash
     */
    public static int HASH_INTERATIONS = 1024;
    private static SecureRandom random = new SecureRandom();

    public static void main(String[] args) {
        getUserSecret("123456", "123456");
    }

    /**
     * 传入未加密密码，如果为空，随机生成。传入盐值, 如果为空，随机生成。返回密码，盐值，和加密后的密码
     *
     * @param password
     * @return
     */
    public static String[] getUserSecret(String password, String pwdSalt) {
        String[] str = new String[3];
        if (StringUtils.isEmpty(password)) {
            password = RadomUtil.buildRandom(PWD_LENGTH);
        }
        // 密码盐值
        byte[] salt;
        // 密码盐值 如果是为空，那么随机获取；不为空，解析pwdSalt
        if (StringUtils.isEmpty(pwdSalt)) {
            salt = generateSalt(SALT_SIZE);
        } else {
            salt = EncodeUtil.hexDecode(pwdSalt);
        }
        // 密码加密
        byte[] hashPassword = DigestsUtil.sha1(password.getBytes(), salt, HASH_INTERATIONS);
        str[0] = password;
        str[1] = EncodeUtil.hexEncode(salt);
        str[2] = EncodeUtil.hexEncode(hashPassword);
        return str;
    }

    /**
     * 生成uuid
     *
     * @return
     */
    public static String createId() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replace("-", "");
    }

    /**
     * 生成随机的Byte[]作为salt.
     *
     * @param numBytes byte数组的大小
     */
    public static byte[] generateSalt(int numBytes) {
        if (numBytes <= 0) {
            throw new UtilException("numBytes argument must be a positive integer (1 or larger)");
        }
        byte[] bytes = new byte[numBytes];
        random.nextBytes(bytes);
        return bytes;
    }
}
