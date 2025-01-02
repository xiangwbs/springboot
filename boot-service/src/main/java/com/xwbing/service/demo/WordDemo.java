package com.xwbing.service.demo;

import cn.hutool.core.io.IoUtil;
import cn.hutool.http.HtmlUtil;
import com.aspose.words.*;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * @author daofeng
 * @version $
 * @since 2025年01月02日 14:24
 */
@Slf4j
public class WordDemo {
    public static void main(String[] args) throws Exception {
        FileInputStream inputStream = new FileInputStream("/Users/xwbing/Downloads/财政政策/43193|正文|规范性文件正文.doc");
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
            // 表格添加边框
            if (element.is("table")) {
                element.attr("border", "1");
            }
        }
        return document.body().html();
    }


    public static InputStream toPdf(InputStream inputStream) throws Exception {
        Document doc = new Document(inputStream);
        File tmpFile = File.createTempFile("tmpFile", ".pdf");
        FileOutputStream fos = new FileOutputStream(tmpFile);
        doc.save(fos, SaveFormat.PDF);
        FileInputStream stream = IoUtil.toStream(tmpFile);
        if (tmpFile.exists()) {
            tmpFile.delete();
        }
        return stream;
    }
}
