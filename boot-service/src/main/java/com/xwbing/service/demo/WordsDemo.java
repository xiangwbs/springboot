package com.xwbing.service.demo;

import cn.hutool.core.io.IoUtil;
import cn.hutool.http.HtmlUtil;
import com.aspose.words.*;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.*;
import java.nio.file.Files;
import java.util.Base64;

/**
 * @author daofeng
 * @version $
 * @since 2025年01月02日 14:24
 */
@Slf4j
public class WordsDemo {
    public static void main(String[] args) throws Exception {
        FileInputStream inputStream = new FileInputStream("/Users/xwbing/Downloads/财政内存溢出pdf/out.doc");
        String richText = toRichText(inputStream);
        System.out.println(richText);
    }

    public static String toHtml(InputStream inputStream) throws Exception {
        Document doc = new Document(inputStream);
        // 同意修订
        doc.acceptAllRevisions();
        // 去除页眉页脚
        for (Section section : doc.getSections()) {
            HeaderFooter footerFirst = section.getHeadersFooters().getByHeaderFooterType(HeaderFooterType.FOOTER_FIRST);
            if (footerFirst != null) {
                footerFirst.remove();
            }
            HeaderFooter footerPrimary = section.getHeadersFooters().getByHeaderFooterType(HeaderFooterType.FOOTER_PRIMARY);
            if (footerPrimary != null) {
                footerPrimary.remove();
            }
            HeaderFooter footerEven = section.getHeadersFooters().getByHeaderFooterType(HeaderFooterType.FOOTER_EVEN);
            if (footerEven != null) {
                footerEven.remove();
            }
            HeaderFooter headerFirst = section.getHeadersFooters().getByHeaderFooterType(HeaderFooterType.HEADER_FIRST);
            if (headerFirst != null) {
                headerFirst.remove();
            }
            HeaderFooter headerPrimary = section.getHeadersFooters().getByHeaderFooterType(HeaderFooterType.HEADER_PRIMARY);
            if (headerPrimary != null) {
                headerPrimary.remove();
            }
            HeaderFooter headerEven = section.getHeadersFooters().getByHeaderFooterType(HeaderFooterType.HEADER_EVEN);
            if (headerEven != null) {
                headerEven.remove();
            }
        }
        // doc转为html
        HtmlSaveOptions options = new HtmlSaveOptions(SaveFormat.HTML);
        options.setExportImagesAsBase64(true);
        options.setPrettyFormat(true);
        return doc.toString(options);
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
            //图片处理
            if (element.is("img")) {
                String src = element.attr("src");
                if (src.startsWith("data:image/")) {
                    String[] parts = src.split(",", 2);
                    if (parts.length != 2) {
                        continue;
                    }
                    // 获取图片类型
                    String imageSuffix = parts[0].split("/")[1].split(";")[0];
                    // 获取Base64编码的图片数据
                    byte[] imageBytes = Base64.getDecoder().decode(parts[1]);
                    // 图片上传
                    ByteArrayInputStream stream = IoUtil.toStream(imageBytes);
//                    element.attr("src", ossUrl);
                }
            }
        }
        return document.body().html();
    }

    public static InputStream toPdf(InputStream inputStream) throws Exception {
        Document doc = new Document(inputStream);
        File tmpFile = File.createTempFile("tmpFile", ".pdf");
        doc.save(Files.newOutputStream(tmpFile.toPath()), SaveFormat.PDF);
        FileInputStream stream = IoUtil.toStream(tmpFile);
        if (tmpFile.exists()) {
            tmpFile.delete();
        }
        return stream;
    }
}