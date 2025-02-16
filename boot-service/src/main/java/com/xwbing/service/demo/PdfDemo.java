package com.xwbing.service.demo;

import cn.hutool.core.io.IoUtil;
import cn.hutool.http.HtmlUtil;
import com.aspose.pdf.DocSaveOptions;
import com.aspose.pdf.Document;
import com.aspose.pdf.HtmlSaveOptions;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import technology.tabula.ObjectExtractor;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

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
        FileInputStream inputStream = new FileInputStream("/Users/xwbing/Downloads/财政内存溢出pdf/带表格.pdf");
//        String richText = toRichText(inputStream);
//        System.out.println(richText);
//        toDoc(inputStream);
        pdfbox(inputStream);
        System.out.println("");
    }

    public static void pdfbox(InputStream inputStream) throws Exception {
        PDDocument doc = PDDocument.load(IoUtil.readBytes(inputStream));
        ObjectExtractor extractor = new ObjectExtractor(doc);
        SpreadsheetExtractionAlgorithm tableExtractor = new SpreadsheetExtractionAlgorithm();
        for (int pageNum = 0; pageNum < doc.getNumberOfPages(); pageNum++) {
            PDPage page = doc.getPage(pageNum);
            PDResources resources = page.getResources();
            // 遍历页面中的所有 XObject
            for (COSName xObjectName : resources.getXObjectNames()) {
                PDXObject xObject = resources.getXObject(xObjectName);
                System.out.println("");
                // 检查 XObject 是否为图像
                if (xObject instanceof PDImageXObject) {
                    PDImageXObject image = (PDImageXObject) xObject;
                    System.out.println("");
                }
            }
        }
    }

    public static void toDoc(InputStream inputStream) {
        Document doc = new Document(inputStream);
        DocSaveOptions docSaveOptions = new DocSaveOptions();
        docSaveOptions.setFormat(DocSaveOptions.DocFormat.DocX);
        docSaveOptions.setMode(DocSaveOptions.RecognitionMode.Flow);
        doc.save("/Users/xwbing/Downloads/财政内存溢出pdf/out.docx", docSaveOptions);
    }

    public static String toHtml(InputStream inputStream) throws Exception {
        Document doc = new Document(inputStream);
        // doc转为html
        HtmlSaveOptions options = new HtmlSaveOptions();
        options.setSplitIntoPages(false);
        options.setPartsEmbeddingMode(HtmlSaveOptions.PartsEmbeddingModes.EmbedAllIntoHtml);
        options.setRasterImagesSavingMode(HtmlSaveOptions.RasterImagesSavingModes.AsEmbeddedPartsOfPngPageBackground);
        options.setFontSavingMode(HtmlSaveOptions.FontSavingModes.SaveInAllFormats);
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
            // 表格添加边框
            if (element.is("table")) {
                element.attr("border", "1");
            }
        }
        return document.body().html();
    }
}