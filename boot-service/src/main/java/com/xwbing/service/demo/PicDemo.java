package com.xwbing.service.demo;

import cn.hutool.core.codec.Base64Encoder;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * @author daofeng
 * @version $
 * @since 2026年01月09日 13:43
 */
public class PicDemo {

    public static String compressByHighQualityScaleBase64(String path, long maxSize) {
        InputStream inputStream = null;
        try {
            int lastIndexOf = path.lastIndexOf("/");
            String urlName = path.substring(lastIndexOf + 1);
            String fileName = urlName.substring(0, urlName.indexOf("."));
            String typeAndParam = urlName.substring(urlName.indexOf("."));
            String urlPath = path.substring(0, lastIndexOf + 1);
            fileName = URLEncoder.encode(fileName, "UTF-8");
            path = urlPath + fileName + typeAndParam;
            URL url = new URL(path);
            URLConnection urlConnection = url.openConnection();
            long fileSize = urlConnection.getContentLengthLong();
            inputStream = urlConnection.getInputStream();
            if (fileSize > maxSize) {
                String type = getFileType(url.getPath());
                BufferedImage oldImage = ImageIO.read(inputStream);
                double scale = 0.99;
                int width = oldImage.getWidth();
                int height = oldImage.getHeight();
                while (width * height > maxSize && scale > 0) {
                    width = (int) (oldImage.getWidth() * scale);
                    height = (int) (oldImage.getHeight() * scale);
                    scale -= 0.01;
                }
                BufferedImage bufferedImage = highQualityScale(oldImage, width, height);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, type, baos);
                return Base64Encoder.encode(baos.toByteArray());
            } else {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int nRead;
                byte[] data = new byte[1024];
                while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                buffer.flush();
                return Base64Encoder.encode(buffer.toByteArray());
            }
        } catch (Exception e) {
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
            }
        }
        return null;
    }

    public static String getFileType(String fileName) {
        if (StringUtils.isNotEmpty(fileName)) {
            int i = fileName.lastIndexOf(".");
            if (i > 0 && fileName.length() - i <= 5) {
                return fileName.substring(i + 1);
            }
        }
        return null;
    }

    public static BufferedImage highQualityScale(BufferedImage original, int targetWidth, int targetHeight) {
        BufferedImage scaledImage = new BufferedImage(
                targetWidth,
                targetHeight,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = scaledImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.drawImage(original, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();
        return scaledImage;
    }
}
