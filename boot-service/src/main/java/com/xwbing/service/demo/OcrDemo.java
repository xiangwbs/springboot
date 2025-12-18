package com.xwbing.service.demo;

import com.aliyun.ocr20191230.models.RecognizeCharacterResponse;
import com.aliyun.ocr20191230.models.RecognizeCharacterResponseBody;

import java.io.InputStream;
import java.net.URL;

/**
 * @author daofeng
 * @version $
 * @since 2025年12月18日 10:47
 */
public class OcrDemo {
    public static com.aliyun.ocr20191230.Client createClient() throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId("")
                .setAccessKeySecret("");
        // 访问的域名
        config.endpoint = "ocr.cn-shanghai.aliyuncs.com";
        return new com.aliyun.ocr20191230.Client(config);
    }

    public static void main(String[] args_) throws Exception {
        captcha("https://xwyq.yjt.zj.gov.cn:9443/ifcs/captcha");
    }

    public static String captcha(String urlStr) throws Exception {
        com.aliyun.ocr20191230.Client client = OcrDemo.createClient();
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
