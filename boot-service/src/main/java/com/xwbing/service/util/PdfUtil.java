package com.xwbing.service.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

/**
 * @author daofeng
 * @version $Id$
 * @since 2021年07月26日 12:36 PM
 */
@Slf4j
public class PdfUtil {
    public static byte[] urlToImage(String url) {
        try (InputStream inputStream = new URL(url).openConnection().getInputStream()) {
            PDDocument doc = PDDocument.load(inputStream);
            PDFRenderer renderer = new PDFRenderer(doc);
            int pageCount = doc.getNumberOfPages();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            for (int i = 0; i < pageCount; i++) {
                BufferedImage image = renderer.renderImageWithDPI(i, 296);
                ImageIO.write(image, "png", os);
            }
            return os.toByteArray();
        } catch (Exception e) {
            log.error("PdfUtil.urlToImage error", e);
            return new byte[0];
        }
    }

    public static byte[] fileToImage(MultipartFile file) {
        try {
            PDDocument doc = PDDocument.load(file.getInputStream());
            PDFRenderer renderer = new PDFRenderer(doc);
            int pageCount = doc.getNumberOfPages();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            for (int i = 0; i < pageCount; i++) {
                BufferedImage image = renderer.renderImageWithDPI(i, 296);
                ImageIO.write(image, "png", os);
            }
            return os.toByteArray();
        } catch (Exception e) {
            log.error("PdfUtil.fileToImage error", e);
            return new byte[0];
        }
    }

    public static byte[] streamToImage(InputStream inputStream) {
        try {
            PDDocument doc = PDDocument.load(inputStream);
            PDFRenderer renderer = new PDFRenderer(doc);
            int pageCount = doc.getNumberOfPages();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            for (int i = 0; i < pageCount; i++) {
                BufferedImage image = renderer.renderImageWithDPI(i, 296);
                ImageIO.write(image, "png", os);
            }
            return os.toByteArray();
        } catch (Exception e) {
            log.error("PdfUtil.streamToImage error", e);
            return new byte[0];
        }
    }
}