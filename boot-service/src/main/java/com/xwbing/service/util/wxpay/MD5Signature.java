/**
 *
 */
package com.xwbing.service.util.wxpay;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.security.SignatureException;


public class MD5Signature {

    public static String sign(String content, String key) throws Exception {

        String tosign = (content == null ? "" : content) + key;

        System.out.println("signdata:" + tosign);
        try {
            return DigestUtils.md5Hex(getContentBytes(tosign, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            throw new SignatureException("MD5签名[content = " + content + "; charset = utf-8" + "]发生异常!", e);
        }

    }

    public static boolean verify(String content, String sign, String key) throws Exception {
        String tosign = (content == null ? "" : content) + key;

        try {
            String mySign = DigestUtils.md5Hex(getContentBytes(tosign, "utf-8"));

            return StringUtils.equals(mySign, sign) ? true : false;
        } catch (UnsupportedEncodingException e) {
            throw new SignatureException("MD5验证签名[content = " + content + "; charset =utf-8 " + "; signature = " + sign + "]发生异常!", e);
        }
    }

    public static boolean verify(String content, String sign, String key, String charset) throws Exception {
        String tosign = (content == null ? "" : content) + key;
        try {
            String mySign = DigestUtils.md5Hex(getContentBytes(tosign, charset));

            return StringUtils.equals(mySign, sign) ? true : false;
        } catch (UnsupportedEncodingException e) {
            throw new SignatureException("MD5验证签名[content = " + content + "; charset =" + charset + "; signature = " + sign + "]发生异常!", e);
        }
    }

    /**
     * @param content
     * @param charset
     * @return
     * @throws SignatureException
     * @throws UnsupportedEncodingException
     */
    protected static byte[] getContentBytes(String content, String charset) throws UnsupportedEncodingException {
        if (StringUtils.isEmpty(charset)) {
            return content.getBytes();
        }

        return content.getBytes(charset);
    }

    public static void main(String[] args) {
        try {
            String str = MD5Signature.sign("123455", "666666666666666");
            System.out.println("qqq:" + str);
            System.out.println(MD5Signature.verify("123455", "6edd55a3421c6324db29690dbc334666", "666666666666666"));

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
