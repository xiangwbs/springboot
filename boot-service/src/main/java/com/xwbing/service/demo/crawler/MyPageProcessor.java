package com.xwbing.service.demo.crawler;

import cn.hutool.json.JSONUtil;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.PlainText;
import us.codecraft.webmagic.selector.Selectable;
import us.codecraft.webmagic.selector.Xpath2Selector;

import javax.swing.text.html.HTML;
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
        String content = page.getRawText();
        Html html = new Html(fillUrl(url, content));
        if (url.equals("https://shuo.news.esnai.com/article/")) {
            List<Selectable> nodes = this.getNodes(html, "//ul[@class=\"pageList2\"]/li");
            nodes.forEach(selectable -> {
                String articleUrl = selectable.xpath("//li/div[@class=\"pageListInner\"]/a/@href").get();
                page.addTargetRequest(articleUrl);
                System.out.println("");
            });
        } else if (url.matches("https://shuo.news.esnai.com/article/\\d*/(\\d*.shtml)?")) {
            String title = this.getNodes(html, "//h1[@class=\"pageTitle\"]/text()").get(0).get();
            String author = this.getNodes(html, "//span[@class=\"counts author\"]/a/text()").get(0).get();
            String publishDate = this.getNodes(html, "//span[@class=\"counts author\"]/text()").get(0).get().replaceAll(" / ", "");
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

    /**
     * 将相对路径的URL补全为绝对路径
     *
     * @param baseUrl
     * @param content
     * @return
     */
    private Document fillUrl(String baseUrl, String content) {
        if (StringUtils.isEmpty(content) || StringUtils.isEmpty(baseUrl)) {
            return new Document("");
        }
        Document doc = Jsoup.parse(content);
        doc.outputSettings().prettyPrint(false);
        Elements aElements = doc.getElementsByTag(HTML.Tag.A.toString());
        for (Element ele : aElements) {
            ele.attr("href", StringUtil.resolve(baseUrl, ele.attr("href")));
        }
        Elements imgElements = doc.getElementsByTag(HTML.Tag.IMG.toString());
        for (Element ele : imgElements) {
            ele.attr("src", StringUtil.resolve(baseUrl, ele.attr("src")));
        }
        return doc;
    }

    /**
     * 解析获得多个节点
     * 当xpath的表达式不能使用时出现异常时，使用Xpath2Selector再次解析
     */
    private List<Selectable> getNodes(Selectable selectable, String xpath) {
        try {
            return selectable.xpath(xpath).nodes();
        } catch (Exception e) {
            Xpath2Selector selector = new Xpath2Selector(xpath);
            List<String> list = selector.selectList(selectable.toString());
            List<Selectable> nodes = new ArrayList<>();
            for (String value : list) {
                nodes.add(new PlainText(value));
            }
            return nodes;
        }
    }
}