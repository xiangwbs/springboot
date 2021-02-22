package com.xwbing.service.demo;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * @author daofeng
 * @version $Id$
 * @since 2021年02月22日 2:23 PM
 */
public class JsonupDemo {
    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet("www.baidu.com");
        CloseableHttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        String webContent = EntityUtils.toString(entity, "utf-8");
        System.out.println("网页内容：" + webContent);
        response.close();

        Document doc = Jsoup.parse(webContent);
        Elements eles = doc.getElementsByTag("title");
        Element element = eles.get(0);
        System.out.println("标题:" + element.text());
        Element ele = doc.getElementById("ftConw");
        System.out.println("文本：" + ele.text());
    }
}
