package com.xwbing.service.demo;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;

/**
 * @author daofeng
 * @version $
 * @since 2026年04月03日 15:42
 */
public class JsonPathDemo {
    public static void main(String[] args) {
        String json = "{\"data\":{\"name\":\"道风\",\"nick\":[\"风哥\",\"道哥\"],\"age\":10,\"xiaoshu\":11.1},\"sucess\":true}";
        String data = parse(json,"data");
        String name = parse(json,"data.name");
        String nick = parse(json,"data.nick");
        String nick0 = parse(json,"data.nick[0]");
        String sucess = parse(json,"sucess");
        String age = parse(json,"data.age");
        String xiaoshu = parse(json,"data.xiaoshu");
        System.out.println("");
    }

    public static String parse(String jsonStr, String path) {
        JSON parse = JSONUtil.parse(jsonStr);
        Object byPath = parse.getByPath(path);
        if (byPath == null) {
            return "";
        }
        if (byPath instanceof String || byPath instanceof Number || byPath instanceof Boolean) {
            return byPath.toString();
        } else {
            return JSONUtil.toJsonStr(byPath);
        }
    }
}