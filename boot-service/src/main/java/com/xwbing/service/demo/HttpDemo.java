package com.xwbing.service.demo;

import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author daofeng
 * @version $
 * @since 2024年06月27日 3:10 PM
 */
@Slf4j
public class HttpDemo {
    private static final Integer TIMEOUT = 15 * 1000;

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
//        String res = HttpUtil.get(url, paramMap, TIMEOUT);
//        res = HttpRequest
//                .get(url)
//                .form(param)
//                .timeout(TIMEOUT)
//                .header("", "")
//                .execute()
//                .body();
//        res = HttpUtil.post(url, JSONUtil.toJsonStr(paramMap), TIMEOUT);
//        res = HttpRequest
//                .post(url)
//                .body(JSONUtil.toJsonStr(paramMap))
//                .timeout(TIMEOUT)
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

    public String addFile(String url, String picUrl) {
        File tmpFile;
        try {
            String suffix = picUrl.substring(picUrl.lastIndexOf("."));
            tmpFile = File.createTempFile("picTmp", suffix);
            HttpUtil.downloadFile(picUrl, tmpFile);
            HashMap<String, Object> paramMap = new HashMap<>();
            paramMap.put("file", tmpFile);
            String result = HttpUtil.post(url, paramMap);
            if (tmpFile.exists()) {
                tmpFile.delete();
            }
            log.info("addFile picUrl:{} result:{}", picUrl, result);
            JSONObject resultObj = JSONUtil.parseObj(result);
            Boolean success = resultObj.getBool("success", false);
            if (!success) {
                return null;
            }
            return resultObj.getStr("data");
        } catch (Exception e) {
            log.info("addFile picUrl:{} error", picUrl);
            return null;
        }
    }
}