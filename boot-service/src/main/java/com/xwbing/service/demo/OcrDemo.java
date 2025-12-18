package com.xwbing.service.demo;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.aliyun.ocr20191230.models.RecognizeCharacterResponse;
import com.aliyun.ocr20191230.models.RecognizeCharacterResponseBody;
import com.aliyun.ocr_api20210707.models.RecognizeGeneralResponse;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.net.URL;

/**
 * @author daofeng
 * @version $
 * @since 2025年12月18日 10:47
 */
public class OcrDemo {
    public static com.aliyun.ocr20191230.Client client2019() throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId("")
                .setAccessKeySecret("");
        config.endpoint = "ocr.cn-shanghai.aliyuncs.com";
        return new com.aliyun.ocr20191230.Client(config);
    }

    public static com.aliyun.ocr_api20210707.Client client2021() throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId("")
                .setAccessKeySecret("");
        config.endpoint = "ocr-api.cn-hangzhou.aliyuncs.com";
        return new com.aliyun.ocr_api20210707.Client(config);
    }

    public static void main(String[] args_) throws Exception {
        String captcha2019 = captcha2019("https://xwyq.yjt.zj.gov.cn:9443/ifcs/captcha", 4, 10);
        System.out.println(captcha2019);
        String captcha2021 = captcha2021("https://xwyq.yjt.zj.gov.cn:9443/ifcs/captcha", 4, 10);
        System.out.println(captcha2021);
    }

    public static String captcha2021(String url, int length, int retry) {
        try {
            com.aliyun.ocr_api20210707.Client client = OcrDemo.client2021();
            com.aliyun.ocr_api20210707.models.RecognizeGeneralRequest recognizeGeneralRequest = new com.aliyun.ocr_api20210707.models.RecognizeGeneralRequest()
                    .setUrl(url);
            com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
            RecognizeGeneralResponse resp = client.recognizeGeneralWithOptions(recognizeGeneralRequest, runtime);
            if (resp.getStatusCode() != 200) {
                return null;
            }
            if (resp.getBody() == null) {
                return null;
            }
            String data = resp.getBody().getData();
            if (StringUtils.isBlank(data)) {
                return null;
            }
            JSONObject dateJson = JSONUtil.parseObj(data);
            String content = dateJson.getStr("content");
            if (StringUtils.isBlank(content)) {
                return null;
            }
            content = content.replaceAll("[^0-9a-zA-Z]", "");
            if (content.length() == length) {
                return content;
            } else {
                if (retry <= 0) {
                    return null;
                }
                return captcha2021(url, length, --retry);
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static String captcha2019(String url, int length, int retry) {
        try {
            com.aliyun.ocr20191230.Client client = OcrDemo.client2019();
            InputStream inputStream = new URL(url).openConnection().getInputStream();
            com.aliyun.ocr20191230.models.RecognizeCharacterAdvanceRequest recognizeCharacterAdvanceRequest = new com.aliyun.ocr20191230.models.RecognizeCharacterAdvanceRequest()
                    .setImageURLObject(inputStream)
                    .setMinHeight(10)
                    .setOutputProbability(true);
            com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
            RecognizeCharacterResponse response = client.recognizeCharacterAdvance(recognizeCharacterAdvanceRequest, runtime);
            if (response.getStatusCode() != 200) {
                return null;
            }
            StringBuilder text = new StringBuilder();
            for (RecognizeCharacterResponseBody.RecognizeCharacterResponseBodyDataResults result : response.getBody().getData().getResults()) {
                text.append(result.getText());
            }
            text = new StringBuilder(text.toString().replaceAll("[^0-9a-zA-Z]", ""));
            if (text.length() == length) {
                return text.toString();
            } else {
                if (retry <= 0) {
                    return null;
                }
                return captcha2019(url, length, --retry);
            }
        } catch (Exception e) {
            return null;
        }
    }
}