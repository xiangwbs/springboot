package com.xwbing.service.demo.crawler;

import us.codecraft.webmagic.Spider;

/**
 * @author daofeng
 * @version $
 * @since 2024年07月30日 2:25 PM
 */
public class CrawlerDemo {
    public static void main(String[] args) {
        Spider.create(new MyPageProcessor())
                .addUrl("https://shuo.news.esnai.com/article/")
                .addPipeline(new MyPipeline())
                .thread(1)
                .run();
    }
}