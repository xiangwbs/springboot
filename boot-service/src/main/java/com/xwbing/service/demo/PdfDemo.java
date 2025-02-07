package com.xwbing.service.demo;

import cn.hutool.http.HtmlUtil;
import com.aspose.pdf.Document;
import com.aspose.pdf.HtmlSaveOptions;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @author daofeng
 * @version $
 * @since 2025年01月02日 14:24
 */
@Slf4j
public class PdfDemo {
    public static void main(String[] args) throws Exception {
        FileInputStream inputStream = new FileInputStream("/Users/xwbing/Downloads/6浙江省城市道路管理办法 （省政府令第404号）.pdf");
        String richText = toRichText(inputStream);
        System.out.println(richText);
    }

    public static String toHtml(InputStream inputStream) throws Exception {
        Document doc = new Document(inputStream);
        // doc转为html
        HtmlSaveOptions options = new HtmlSaveOptions();
        options.setSplitIntoPages(false);
        options.setPartsEmbeddingMode(HtmlSaveOptions.PartsEmbeddingModes.EmbedAllIntoHtml);
        options.setRasterImagesSavingMode(HtmlSaveOptions.RasterImagesSavingModes.AsEmbeddedPartsOfPngPageBackground);
        File tmpFile = File.createTempFile("tmpPdf", ".html");
        doc.save(tmpFile.getPath(), options);
        String html = Jsoup.parse(tmpFile, "utf-8").body().html();
        if (tmpFile.exists()) {
            tmpFile.delete();
        }
        return html;
    }

    public static String toRichText(InputStream inputStream) throws Exception {
        String html = toHtml(inputStream);
        // 去除a标签不包括内容
        html = HtmlUtil.unwrapHtmlTag(html, "a");
        org.jsoup.nodes.Document document = Jsoup.parse(html);
        for (Element element : document.getAllElements()) {
            // 去除所有样式
            element.removeAttr("style");
//            // 去除定位样式
//            String style = element.attr("style");
//            if (style != null && style.contains("position")) {
//                style = style.replaceAll("position\\s*:\\s*[^;]+;?", "");
//                element.attr("style", style);
//            }
            // 表格添加边框
            if (element.is("table")) {
                element.attr("border", "1");
            }
        }
        return document.body().html();
    }
}