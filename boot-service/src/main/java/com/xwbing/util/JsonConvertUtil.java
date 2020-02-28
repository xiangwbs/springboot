package com.xwbing.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @author xiangwb
 */
public class JsonConvertUtil {
    public static JSONArray str2JsonArray(String string) {
        return JSONObject.parseArray(string);
    }

    public static JSONArray obj2JsonArray(Object object) {
        return JSONObject.parseArray(JSONObject.toJSONString(object));
    }

    public static JSONObject str2JsonObj(String string) {
        return JSONObject.parseObject(string);
    }

    public static JSONObject obj2JsonObj(Object object) {
        return JSONObject.parseObject(JSONObject.toJSONString(object));
    }
}
