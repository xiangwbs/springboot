package com.xwbing.service.demo;

import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.util.URLUtil;
import org.apache.commons.collections4.MapUtils;

import java.util.HashMap;

/**
 * @author daofeng
 * @version $
 * @since 2024年06月27日 3:10 PM
 */
public class HttpDemo {
    public static void main(String[] args) {
        String url = UrlBuilder.create()
                .setScheme("http")
                .setHost("www.xwbing.com")
                .addPath("/page")
                .build();
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", "道风");
        paramMap.put("age", 18);
        url = addParam(url, paramMap);
//        String res = HttpUtil.get(url, paramMap);
//        res = HttpRequest
//                .get(url)
//                .form(param)
//                .timeout(5 * 1000)
//                .header("", "")
//                .execute()
//                .body();
//        res = HttpUtil.post(url, JSONUtil.toJsonStr(paramMap));
//        res = HttpRequest
//                .post(url)
//                .body(JSONUtil.toJsonStr(paramMap))
//                .timeout(5 * 1000)
//                .header("", "")
//                .execute()
//                .body();
        System.out.println("");
    }

    public static String addParam(String url, HashMap<String, Object> paramMap) {
        UrlBuilder urlBuilder = UrlBuilder.of(url);
        if (MapUtils.isNotEmpty(paramMap)) {
            paramMap.forEach(urlBuilder::addQuery);
        }
        return URLUtil.decode(urlBuilder.toString());
    }
}