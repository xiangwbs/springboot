package com.xwbing.service.demo;

import org.ofdrw.converter.ConvertHelper;
import org.ofdrw.reader.OFDReader;

/**
 * @author daofeng
 * @version $
 * @since 2025年02月08日 14:12
 */
public class OdfDemo {
    public static void main(String[] args) throws Exception {
        OFDReader ofdReader = new OFDReader("/Users/xwbing/Downloads/市规划和自然资源局反馈意见.ofd");
        ConvertHelper.toHtml(ofdReader, "/Users/xwbing/Downloads/ofd.html", 800);
        System.out.println("");
    }
}
