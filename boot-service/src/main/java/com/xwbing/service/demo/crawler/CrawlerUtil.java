package com.xwbing.service.demo.crawler;

import cn.hutool.core.util.ObjectUtil;
import com.gargoylesoftware.css.parser.CSSErrorHandler;
import com.gargoylesoftware.css.parser.CSSException;
import com.gargoylesoftware.css.parser.CSSParseException;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.DefaultJavaScriptErrorListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.PlainText;
import us.codecraft.webmagic.selector.Selectable;
import us.codecraft.webmagic.selector.Xpath2Selector;

import javax.swing.text.html.HTML;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author daofeng
 * @version $
 * @since 2024年07月30日 5:57 PM
 */
@Slf4j
public class CrawlerUtil {
    /**
     * 解析获得多个节点
     * 当xpath的表达式不能使用时出现异常时，使用Xpath2Selector再次解析
     */
    public static List<Selectable> getNodes(Selectable selectable, String xpath) {
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

    /**
     * 获取页面html
     *
     * @param page
     * @param enableScript
     * @return
     */
    public static Html getHtml(Page page, Boolean enableScript) {
        String url = page.getRequest().getUrl();
        String content = "";
        if (enableScript) {
            WebClient webClient = null;
            HtmlPage htmlPage = null;
            try {
                webClient = getWebClient();
                htmlPage = webClient.getPage(url);
                TimeUnit.SECONDS.sleep(3);
                content = htmlPage.asXml();
            } catch (Exception e) {
                log.error("script执行失败", e);
                content = page.getRawText();
            } finally {
                if (ObjectUtil.isNotNull(htmlPage)) {
                    htmlPage.remove();
                }
                if (ObjectUtil.isNotNull(webClient)) {
                    webClient.close();
                }
            }
        } else {
            content = page.getRawText();
        }
        return new Html(fillUrl(url, content));
    }

    /**
     * 将相对路径的URL补全为绝对路径
     *
     * @param baseUrl
     * @param content
     * @return
     */
    private static Document fillUrl(String baseUrl, String content) {
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


    public static WebClient getWebClient() {
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setPrintContentOnFailingStatusCode(false);
        webClient.setCssErrorHandler(new MyCssErrorHandler());
        webClient.setJavaScriptErrorListener(new MyJsErrorListener());
        webClient.getOptions().setTimeout(10 * 1000);
        webClient.waitForBackgroundJavaScript(30 * 1000);
        return webClient;
    }

    public static class MyCssErrorHandler implements CSSErrorHandler {

        @Override
        public void warning(CSSParseException e) throws CSSException {

        }

        @Override
        public void error(CSSParseException e) throws CSSException {

        }

        @Override
        public void fatalError(CSSParseException e) throws CSSException {

        }
    }

    public static class MyJsErrorListener extends DefaultJavaScriptErrorListener {
        @Override
        public void scriptException(HtmlPage page, ScriptException scriptException) {
        }

        @Override
        public void timeoutError(HtmlPage page, long allowedTime, long executionTime) {
        }

        @Override
        public void malformedScriptURL(HtmlPage page, String url, MalformedURLException malformedURLException) {

        }

        @Override
        public void loadScriptError(HtmlPage page, URL scriptUrl, Exception exception) {

        }
    }
}