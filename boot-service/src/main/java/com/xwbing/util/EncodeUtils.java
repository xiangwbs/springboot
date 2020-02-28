package com.xwbing.util;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Base64;

/**
 * 说明:编码解码工具类
 * 作者: xiangwb
 */
public class EncodeUtils {
    private static final String DEFAULT_URL_ENCODING = "UTF-8";
    private static final Logger LOGGER = LoggerFactory.getLogger(EncodeUtils.class);

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
            LOGGER.error(e.getMessage());
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
     * Base64编码, URL安全(将Base64中的URL非法字符如+,/=转为其他字符, 见RFC3548).
     */
    public static String base64UrlSafeEncode(byte[] input) {
        return java.util.Base64.getUrlEncoder().encodeToString(input);
    }

    /**
     * Base64解码.
     */
    public static byte[] base64Decode(String input) {
        return Base64.getDecoder().decode(input);
    }

    /**
     * URL 编码, Encode默认为UTF-8.
     */
    public static String urlEncode(String input) {
        try {
            return URLEncoder.encode(input, DEFAULT_URL_ENCODING);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error(e.getMessage());
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
            LOGGER.error(e.getMessage());
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
