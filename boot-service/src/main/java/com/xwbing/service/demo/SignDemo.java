package com.xwbing.service.demo;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.http.HttpRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * @author daofeng
 * @version $
 * @since 2025年03月21日 15:33
 */
public class SignDemo {

    public static void auth() {
        String appKey = "";
        String appSecret = "";
        // 若要简化流程，appToken可以省略
        String appToken = "";
        long timestamp = System.currentTimeMillis();
        String sign = DigestUtil.md5Hex(appKey + appSecret + appToken + timestamp);
        Map<String, Object> param = new HashMap<>();
        param.put("appKey", appKey);
        param.put("sign", sign);
        param.put("timestamp", timestamp);
        String res = HttpRequest
                .get("")
                .form(param)
                .header("appToken", appToken)
                .execute()
                .body();
    }

    public static void main(String[] args) {


    }
}
