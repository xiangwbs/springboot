package com.xwbing.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringEscapeUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 编码解码工具类
 *
 * @author xiangwb
 */
@Slf4j
public class EncodeUtil {
    private static final String DEFAULT_URL_ENCODING = "UTF-8";

    /**
     * Hex编码.
     */
    public static String hexEncode(byte[] input) {
        return Hex.encodeHexString(input);
    }

    /**
     * Hex解码.
     */
    public static byte[] hexDecode(String input) {
        try {
            return Hex.decodeHex(input.toCharArray());
        } catch (DecoderException e) {
            log.error(e.getMessage());
            throw new IllegalStateException("Hex Decoder exception");
        }
    }


    /**
     * Base64编码.
     */
    public static String base64Encode(byte[] input) {
        return Base64.getEncoder().encodeToString(input);
    }

    /**
     * Base64解码.
     */
    public static byte[] base64Decode(String input) {
        return Base64.getDecoder().decode(input);
    }

    /**
     * 是否被base编码过
     *
     * @param base64
     * @return
     */
    public static boolean isBase64(String base64) {
        return org.apache.commons.codec.binary.Base64.isBase64(base64);
    }

    /**
     * Base64编码, URL安全
     * 其中将不使用'= \n \r'填充，并且将标准Base64的'+'和'/'字符分别替换为'-'和'_'
     */
    public static String base64UrlSafeEncode(byte[] input) {
        return Base64.getUrlEncoder().encodeToString(input);
    }

    /**
     * Base64解码, URL安全
     */
    public static byte[] base64UrlDecode(String input) {
        return Base64.getUrlDecoder().decode(input);
    }


    /**
     * URL 编码, Encode默认为UTF-8.
     */
    public static String urlEncode(String input) {
        try {
            return URLEncoder.encode(input, DEFAULT_URL_ENCODING);
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException("Unsupported Encoding Exception");
        }
    }

    /**
     * URL 解码, Encode默认为UTF-8.
     */
    public static String urlDecode(String input) {
        try {
            return URLDecoder.decode(input, DEFAULT_URL_ENCODING);
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException("Unsupported Encoding Exception");
        }
    }

    /**
     * Xml 转码.
     */
    public static String xmlEscape(String xml) {
        return StringEscapeUtils.escapeXml(xml);
    }

    /**
     * Xml 解码.
     */
    public static String xmlUnescape(String xmlEscaped) {
        return StringEscapeUtils.unescapeXml(xmlEscaped);
    }
}
