package com.xwbing.service.demo;

import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.util.URLUtil;
import org.apache.commons.collections4.MapUtils;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author daofeng
 * @version $
 * @since 2024年06月27日 3:10 PM
 */
public class HttpDemo {
    public static void main(String[] args) throws MalformedURLException {
        String url = UrlBuilder.create()
                .setScheme("http")
                .setHost("www.xwbing.com")
                .addPath("/page")
                .build();
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("age", 18);
        paramMap.put("name", "道风");
        url = addParam(url, paramMap, true);
        Map<String, Object> param = getParam(url);
        UrlBuilder urlBuilder = UrlBuilder.of(url);
        String paramStr = URLUtil.decode(urlBuilder.getQueryStr());//name=道风&age=18
        String pathStr = urlBuilder.getPathStr();// /page
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

    public static String addParam(String url, HashMap<String, Object> paramMap, boolean decode) {
        UrlBuilder urlBuilder = UrlBuilder.of(url);
        if (MapUtils.isNotEmpty(paramMap)) {
            paramMap.forEach(urlBuilder::addQuery);
        }
        url = urlBuilder.build();
        if (decode) {
            url = URLUtil.decode(url);
        }
        return url;
    }

    public static Map<String, Object> getParam(String url) {
        UrlBuilder urlBuilder = UrlBuilder.of(url);
        return urlBuilder.getQuery().getQueryMap().entrySet().stream().collect(Collectors.toMap(entry -> String.valueOf(entry.getKey()), Map.Entry::getValue));
    }
}