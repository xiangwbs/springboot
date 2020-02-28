package com.xwbing.util;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.xwbing.exception.UtilException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.springframework.core.io.ClassPathResource;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * 二维码工具类
 *
 * @author xiangwb
 */
@Slf4j
public class QRCodeUtil {
    private static QRCodeUtil.Internal internal = new QRCodeUtil.Internal();

    /***
     * 生成默认尺寸不带logo的二维码
     * @param text 二维码内容
     * @param output 图片文件
     */
    public static void createCode(String text, File output) {
        createCode(text, 200, 200, output);
    }

    /**
     * 自定义尺寸不带logo的二维码
     *
     * @param text   二维码内容
     * @param width  宽
     * @param height 高
     * @param output 图片文件
     */
    public static void createCode(String text, int width, int height, File output) {
        try {
            System.out.println(output.getParentFile().getAbsolutePath());
            internal._codeHeight = height;
            internal._codeWidth = width;
            internal._output = output;
            internal.output(output, internal.createCode(text));
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new UtilException("生成自定义尺寸不带logo的二维码失败");
        }
    }

    /**
     * 生成默认尺寸带logo的二维码
     *
     * @param output  图片文件
     * @param text    二维码内容
     * @param logoImg logo图片
     * @param title   二维码图片描述
     */
    public static void createCodeLogo(File output, String text, InputStream logoImg, String title) {
        createCodeLogo(output, text, logoImg, title, 200, 200, 14);
    }

    /**
     * 生成自定义尺寸带logo的二维码
     *
     * @param output  图片文件
     * @param width   宽
     * @param height  高
     * @param logoImg logo图片
     * @param text    二维码内容
     * @param title   二维码图片描述
     * @param size    字体大小
     */
    public static void createCodeLogo(File output, String text, InputStream logoImg, String title, int width, int height, int size) {
        try {
            internal._codeHeight = height;
            internal._codeWidth = width;
            internal._logoImg = logoImg;
            internal._text = title;
            internal._fontSize = size;
            internal._output = output;
            BitMatrix bitMatrix = internal.createCode(text);
            internal.addMaterial(bitMatrix);
        } catch (WriterException e) {
            log.error(e.getMessage());
            throw new UtilException("生成自定义尺寸带logo的二维码失败");
        }

    }

    /**
     * 解析二维码图片
     *
     * @param file 二维码文件
     * @return
     */
    public static String decode(File file) {
        try {
            if (file == null) {
                return null;
            }
            MultiFormatReader formatReader = new MultiFormatReader();
            BufferedImage image = ImageIO.read(file);
            BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));
            Hashtable<DecodeHintType, Object> hints = new Hashtable<>();
            hints.put(DecodeHintType.CHARACTER_SET, "utf-8");
            Result result = formatReader.decode(binaryBitmap, hints);
            return result.getText();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new UtilException("解析二维码图片失败");
        }
    }

    /**
     * 二维码信息内部类
     */
    private static class Internal {
        // 二维码大小
        private int _codeWidth;
        private int _codeHeight;
        //二维码导出位置
        private File _output;
        //logo文件流
        private InputStream _logoImg;
        //标题内容,字体大小
        private String _text;
        private int _fontSize;
        //logo大小
        private int _logoWidth;
        private int _logoHeight;
        //logo x,y坐标
        private int _logoX;
        private int _logoY;
        //文字x,y坐标
        private int _textX;
        private int _textY;
        //图像类型
        private final String _format = "png";

        /**
         * 根据内容生成二维码矩阵
         *
         * @param text
         * @return
         * @throws WriterException
         */
        private BitMatrix createCode(String text) throws WriterException {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
            return new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, _codeWidth, _codeHeight, hints);// 生成矩阵
        }

        /***
         * 输出生成的二维码
         * @param output
         * @param bitMatrix
         * @throws IOException
         */
        private void output(File output, BitMatrix bitMatrix) throws IOException {
            Path path = FileSystems.getDefault().getPath(output.getParentFile().getAbsolutePath(), output.getName());
            MatrixToImageWriter.writeToPath(bitMatrix, _format, path);// 输出图像
        }

        /***
         * 输出带特殊需求的二维码
         * @throws IOException
         */
        private void output(BufferedImage image) throws IOException {
            if (!ImageIO.write(image, _format, _output)) {
                throw new IOException("Could not write an image of format " + _format + " to " + _output);
            }
        }

        /**
         * 在二维码基础上添加需求
         *
         * @param bitMatrix
         */
        private void addMaterial(BitMatrix bitMatrix) {
            try {
                BufferedImage image = toBufferedImage(bitMatrix);
                //计算高宽
                computeWidthHeight();
                //定位logo
                image = position_logo(image);
                //定位文字
                image = position_text(image);
                //生成二维码
                output(image);
//                return bufferImage_to_InputStream(image);
            } catch (IOException c) {
                log.error(c.getMessage());
//                return null;
            }
        }

        /**
         * 定位文字
         *
         * @param image
         * @return
         */
        private BufferedImage position_text(BufferedImage image) {
            Graphics2D gh = image.createGraphics();
            gh.setColor(Color.black);
            //如果linux上没有该字体的话，会出现类似乱码的情况
            //Font.SANS_SERIF
            gh.setFont(new Font("Microsoft YaHei", Font.BOLD, _fontSize));
            gh.setComposite(AlphaComposite.getInstance(
                    AlphaComposite.SRC_IN, 1.0f));
            gh.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            gh.drawString(_text, 20, _fontSize);
            return image;
        }

        /**
         * 定位logo
         *
         * @param image
         * @return
         * @throws IOException
         */
        private BufferedImage position_logo(BufferedImage image) throws IOException {
            Graphics2D gs = image.createGraphics();
            //载入logo
            BufferedImage logo = changeWidthHeight();
            gs.drawImage(logo, _logoX, _logoY, null);
            gs.dispose();
            logo.flush();
            return image;
        }

        /**
         * 改变图片大小
         *
         * @return
         * @throws IOException
         */
        private BufferedImage changeWidthHeight() throws IOException {
            BufferedImage img = ImageIO.read(_logoImg);
            BufferedImage newLogo = new BufferedImage(_logoWidth, _logoHeight, BufferedImage.TYPE_INT_BGR);
            Graphics graphics = newLogo.createGraphics();
            graphics.drawImage(img, 0, 0, _logoWidth, _logoHeight, null);
            graphics.setColor(Color.white);
            graphics.drawRect(0, 0, _logoWidth - 1, _logoHeight - 1);
            graphics.drawRect(1, 1, _logoWidth - 1, _logoHeight - 1);
            graphics.drawRect(0, 0, _logoWidth - 2, _logoHeight - 2);
            img.flush();
            return newLogo;
        }

        private void computeWidthHeight() {
            //计算logo大小
            _logoHeight = _codeHeight * 2 / 10;
            _logoWidth = _codeWidth * 2 / 10;
            //计算logo坐标
            _logoX = (_codeWidth - _logoWidth) / 2;
            _logoY = (_codeHeight - _logoHeight) / 2;
        }


        /**
         * BitMatrix转BufferedImage
         *
         * @param matrix
         * @return
         */
        private BufferedImage toBufferedImage(BitMatrix matrix) {
            BufferedImage image = new BufferedImage(matrix.getWidth(), matrix.getHeight(), BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < matrix.getWidth(); x++) {
                for (int y = 0; y < matrix.getHeight(); y++) {
                    image.setRGB(x, y, matrix.get(x, y) ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
                }
            }
            return image;
        }

        /**
         * 二维码图片转化为输入流
         *
         * @param image
         * @return
         * @throws IOException
         */
        private InputStream bufferImageToInputStream(BufferedImage image) throws IOException {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(image, _format, os);
            return new ByteArrayInputStream(os.toByteArray());
        }
    }

    public static void main(String[] args) throws Exception {
        ClassPathResource file = new ClassPathResource("pic");
        String path = file.getFile().getAbsolutePath();
        File image = new File(path + File.separator + "xwbing.png");
//        createCode("xiangwbs@163.com", image);
        FileInputStream logo = new FileInputStream(image);
        File out = new File(path + File.separator + "QRCode.png");
        createCodeLogo(out, "java生成的二維碼", logo, "logo");
    }
}

