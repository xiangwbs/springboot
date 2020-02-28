package com.xwbing.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * PropertiesUtil
 *
 * @author xiangwb
 */

public class PropertiesUtil {

    /**
     * 根据Key读取Value
     *
     * @param key    key
     * @param target 地址
     * @return
     */
    public static String getValueByKey(String key, String target) {
        Properties pps = new Properties();
        String filePath = PropertiesUtil.class.getClassLoader().getResource(target).getPath();
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(filePath));
            pps.load(in);
            String value = pps.getProperty(key);
            in.close();
            return value;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
