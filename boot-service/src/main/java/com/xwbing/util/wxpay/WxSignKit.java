package com.xwbing.util.wxpay;

import java.util.*;

public class WxSignKit {
    /***
     * 构建jsapi的签名
     * @param params
     * @return
     */
    public static String buildJsApiSign(Map<String, String> params) {
        StringBuffer sb = new StringBuffer();
        Map<String, String> result = paramsFilter(params);
        String prestr = createLinkString(result); // 把数组所有元素，按照“参数参数值”的模式拼接成字符串
        String encodeKey = SHA1.encode(prestr);
        return encodeKey;
    }


    /***
     * 构建签名参数
     *
     * @param params
     *            请求参数
     * @param key
     *            秘钥
     * @return 签名sign
     */
    public static String buildSign(Map<String, String> params, String key) {
        Map<String, String> result = paramsFilter(params);
        String prestr = createLinkString(result); // 把数组所有元素，按照“参数参数值”的模式拼接成字符串
        System.out.println(prestr + "===========");
        prestr = prestr + "&key=" + key; // 把接口密钥+拼接后的字符串直接连接起来
        System.out.println("===========" + prestr + "===========");
        String mysign = Md5Encrypt.md5(prestr);
        if (mysign != null) {
            mysign = mysign.toUpperCase();
        }
        return mysign;
    }

    /***
     * 除去数组中的空值和签名参数
     *
     * @param sArray
     * @return
     */
    private static Map<String, String> paramsFilter(Map<String, String> sArray) {
        Map<String, String> result = new HashMap<String, String>();
        if (sArray == null || sArray.size() <= 0) {
            return result;
        }
        for (String key : sArray.keySet()) {
            String value = sArray.get(key);
            if (value == null || value.equals("")
                    || key.equalsIgnoreCase("sign")) {
                continue;
            }
            result.put(key, value);
        }
        return result;
    }

    /**
     * 把数组所有元素排序，并按照“参数参数值”的模式拼接成字符串
     *
     * @param params 需要排序并参与字符拼接的参数组
     * @return 拼接后字符串
     */
    private static String createLinkString(Map<String, String> params) {
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        String prestr = "";
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
            sb.append(key).append("=").append(value);
            prestr = prestr + key + "=" + value + "&";
            if (i < keys.size() - 1) {
                sb.append("&");
            }
        }
        return sb.toString();
    }
}
