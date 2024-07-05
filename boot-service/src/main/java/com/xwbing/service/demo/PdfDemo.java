package com.xwbing.service.demo;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.font.FontProvider;

import java.io.FileOutputStream;

/**
 * @author daofeng
 * @version $
 * @since 2024年07月04日 3:29 PM
 */
public class PdfDemo {

    public static void main(String[] args) {
        String sourceHtml = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n" +
                "<html>\n" +
                "<head>\n" +
                "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n" +
                "</head>\n" +
                "<body screen_capture_injected=\"true\" ryt11773=\"1\">\n" +
                "    <p>\n" +
                "        <span style=\"font-size:12.0pt; font-family:MS Mincho\">長空</span> <span\n" +
                "            style=\"font-size:12.0pt; font-family:Times New Roman,serif\">(Broken\n" +
                "            Sword),</span> <span style=\"font-size:12.0pt; font-family:MS Mincho\">秦王殘劍</span>\n" +
                "        <span style=\"font-size:12.0pt; font-family:Times New Roman,serif\">(Flying\n" +
                "            Snow),</span> <span style=\"font-size:12.0pt; font-family:MS Mincho\">飛雪</span>\n" +
                "        <span style=\"font-size:12.0pt; font-family:Times New Roman,serif\">(Moon),\n" +
                "        </span> <span style=\"font-size:12.0pt; font-family:MS Mincho\">如月</span> <span\n" +
                "            style=\"font-size:12.0pt; font-family:Times New Roman,serif\">(the\n" +
                "            King), and</span> <span style=\"font-size:12.0pt; font-family:MS Mincho\">秦王</span>\n" +
                "        <span style=\"font-size:12.0pt; font-family:Times New Roman,serif\">(Sky).</span>\n" +
                "    </p>\n" +
                "</body>\n" +
                "</html>";
        String destPdf = "output.pdf";
        convertHtmlToPdf(sourceHtml, destPdf);
    }

    private static void convertHtmlToPdf(String htmlContent, String destPath) {
        try (FileOutputStream outputStream = new FileOutputStream(destPath)) {
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdf = new PdfDocument(writer);
            ConverterProperties converterProperties = new ConverterProperties();
            FontProvider fontProvider = new FontProvider();
            PdfFont sysFont = PdfFontFactory.createFont("STSongStd-Light", "UniGB-UCS2-H");
            fontProvider.addFont(sysFont.getFontProgram(), "UniGB-UCS2-H");
            converterProperties.setFontProvider(fontProvider);
            HtmlConverter.convertToPdf(htmlContent, pdf, converterProperties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
