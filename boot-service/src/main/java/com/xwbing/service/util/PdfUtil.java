package com.xwbing.service.util;

import cn.hutool.core.io.IoUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author daofeng
 * @version $Id$
 * @since 2021年07月26日 12:36 PM
 */
@Slf4j
public class PdfUtil {
    public static byte[] urlToImage(String url) {
        try (InputStream inputStream = new URL(url).openConnection().getInputStream()) {
            PDDocument doc = Loader.loadPDF(IoUtil.readBytes(inputStream));
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

    public static List<byte[]> fileToImage(MultipartFile file) {
        List<byte[]> list  =new ArrayList<>();
        try {
            PDDocument doc = Loader.loadPDF(IoUtil.readBytes(file.getInputStream()));
            PDFRenderer renderer = new PDFRenderer(doc);
            int pageCount = doc.getNumberOfPages();
            for (int i = 0; i < pageCount; i++) {
                BufferedImage image = renderer.renderImageWithDPI(i, 296);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                ImageIO.write(image, "png", os);
                list.add(os.toByteArray());
            }
            return list;
        } catch (Exception e) {
            log.error("PdfUtil.fileToImage error", e);
            return Collections.emptyList();
        }
    }

    public static byte[] streamToImage(InputStream inputStream) {
        try {
            PDDocument doc = Loader.loadPDF(IoUtil.readBytes(inputStream));
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