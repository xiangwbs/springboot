package com.xwbing.service.demo;

import com.aliyun.ocr20191230.models.RecognizeCharacterResponse;
import com.aliyun.ocr20191230.models.RecognizeCharacterResponseBody;
import com.aliyun.ocr_api20210707.models.RecognizeGeneralResponse;

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
        config.endpoint = "ocr.cn-shanghai.aliyuncs.com";
        return new com.aliyun.ocr_api20210707.Client(config);
    }

    public static void main(String[] args_) throws Exception {
        String captcha2019 = captcha2019("https://xwyq.yjt.zj.gov.cn:9443/ifcs/captcha");
        System.out.println(captcha2019);
        captcha2021("https://xwyq.yjt.zj.gov.cn:9443/ifcs/captcha");
    }

    public static void captcha2021(String url) throws Exception {
        com.aliyun.ocr_api20210707.Client client = OcrDemo.client2021();
        com.aliyun.ocr_api20210707.models.RecognizeGeneralRequest recognizeGeneralRequest = new com.aliyun.ocr_api20210707.models.RecognizeGeneralRequest()
                .setUrl(url);
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        RecognizeGeneralResponse resp = client.recognizeGeneralWithOptions(recognizeGeneralRequest, runtime);
        System.out.println(com.aliyun.teautil.Common.toJSONString(resp.body.data));
    }

    public static String captcha2019(String urlStr) throws Exception {
        com.aliyun.ocr20191230.Client client = OcrDemo.client2019();
        URL url = new URL(urlStr);
        InputStream inputStream = url.openConnection().getInputStream();
        com.aliyun.ocr20191230.models.RecognizeCharacterAdvanceRequest recognizeCharacterAdvanceRequest = new com.aliyun.ocr20191230.models.RecognizeCharacterAdvanceRequest()
                .setImageURLObject(inputStream)
                .setMinHeight(10)
                .setOutputProbability(true);
        com.aliyun.teautil.models.RuntimeOptions runtime = new com.aliyun.teautil.models.RuntimeOptions();
        // 复制代码运行请自行打印 API 的返回值
        RecognizeCharacterResponse response = client.recognizeCharacterAdvance(recognizeCharacterAdvanceRequest, runtime);
        StringBuilder text = new StringBuilder();
        for (RecognizeCharacterResponseBody.RecognizeCharacterResponseBodyDataResults result : response.getBody().getData().getResults()) {
            text.append(result.getText());
        }
        text = new StringBuilder(text.toString().replaceAll("[^0-9]", ""));
        return text.toString();
    }
}