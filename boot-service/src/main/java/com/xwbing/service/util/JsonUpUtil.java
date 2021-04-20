package com.xwbing.service.util;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author daofeng
 * @version $Id$
 * @since 2021年02月22日 2:27 PM
 */
@Slf4j
public class JsonUpUtil {
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HtmlVO {
        private String title;
        private String description;
        private String logo;
    }

    /**
     * 获取网页信息
     *
     * @param url
     *
     * @return
     */
    public static HtmlVO getHtmlInfo(String url) {
        try {
            Document doc = Jsoup.connect(url).get();
            // Elements titles = doc.getElementsByTag("title");
            // String title = titles.isEmpty() ? null : titles.get(0).text();
            String title = doc.title();
            Elements descriptions = doc.select("meta[name=description]");
            String description = descriptions.isEmpty() ? null : descriptions.get(0).attr("content");
            String[] urls = url.split("/");
            return HtmlVO.builder().title(title).description(description)
                    .logo(urls[0] + "//" + urls[2] + "/" + "favicon.ico").build();
        } catch (IOException e) {
            log.error("getHtmlInfo error ", e);
            return HtmlVO.builder().build();
        }
    }

    public static void main(String[] args) {
        HtmlVO htmlInfo = getHtmlInfo("https://www.baidu.com");
        System.out.println(htmlInfo);
    }
}
