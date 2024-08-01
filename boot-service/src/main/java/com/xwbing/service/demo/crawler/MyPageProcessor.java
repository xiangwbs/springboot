package com.xwbing.service.demo.crawler;

import cn.hutool.json.JSONUtil;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import java.util.*;

/**
 * @author daofeng
 * @version $
 * @since 2024年07月30日 2:27 PM
 */
public class MyPageProcessor implements PageProcessor {
    @Override
    public void process(Page page) {
        // 处理页面逻辑
        String url = page.getRequest().getUrl();
        Html html = CrawlerUtil.getHtml(page, false);
        if (url.equals("https://shuo.news.esnai.com/article/")) {
            List<Selectable> nodes = CrawlerUtil.getNodes(html, "//ul[@class=\"pageList2\"]/li");
            nodes.forEach(selectable -> {
                String articleUrl = selectable.xpath("//li/div[@class=\"pageListInner\"]/a/@href").get();
                page.addTargetRequest(articleUrl);
                System.out.println("");
            });
        } else if (url.matches("https://shuo.news.esnai.com/article/\\d*/(\\d*.shtml)?")) {
            String title = CrawlerUtil.getNodes(html, "//h1[@class=\"pageTitle\"]/text()").get(0).get();
            String author = CrawlerUtil.getNodes(html, "//span[@class=\"counts author\"]/a/text()").get(0).get();
            String publishDate = CrawlerUtil.getNodes(html, "//span[@class=\"counts author\"]/text()").get(0).get().replaceAll(" / ", "");
            Map<String, Object> result = new HashMap<>();
            result.put("title", title);
            result.put("author", author);
            result.put("publishDate", publishDate);
            result.put("linkUrl", url);
            page.putField("result", JSONUtil.toJsonStr(result));
            System.out.println("");
        }
        System.out.println("");
    }

    @Override
    public Site getSite() {
        Set<Integer> acceptStatCode = new HashSet<>();
        acceptStatCode.add(200);
        return Site.me()
                .setAcceptStatCode(acceptStatCode)
                .setRetryTimes(3)
                .setSleepTime(300)
                .setCycleRetryTimes(3)
                .setRetrySleepTime(30000)
                .setTimeOut(30000)
                .setCharset("UTF-8")
                .setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36")
                ;
    }
}