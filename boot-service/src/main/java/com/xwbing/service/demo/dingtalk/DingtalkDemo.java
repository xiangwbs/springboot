package com.xwbing.service.demo.dingtalk;

import cn.hutool.json.JSONUtil;
import com.aliyun.dingtalkai_paa_s_1_0.Client;
import com.aliyun.dingtalkai_paa_s_1_0.models.LiandanluExclusiveModelHeaders;
import com.aliyun.dingtalkai_paa_s_1_0.models.LiandanluExclusiveModelRequest;
import com.aliyun.dingtalkai_paa_s_1_0.models.LiandanluExclusiveModelResponse;
import com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenResponse;
import com.aliyun.tea.TeaException;
import com.aliyun.teaopenapi.models.Config;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author daofeng
 * @version $
 * @since 2024年02月02日 4:59 PM
 */
@Slf4j
public class DingtalkDemo {
    private static final Config CONFIG;

    static {
        CONFIG = new Config();
        CONFIG.protocol = "https";
        CONFIG.regionId = "central";
    }

    public static String getAccessToken(String appKey, String appSecret) {
        try {
            com.aliyun.dingtalkoauth2_1_0.Client client = new com.aliyun.dingtalkoauth2_1_0.Client(CONFIG);
            com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenRequest getAccessTokenRequest = new com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenRequest()
                    .setAppKey(appKey)
                    .setAppSecret(appSecret);
            GetAccessTokenResponse accessToken = client.getAccessToken(getAccessTokenRequest);
            return getBody(accessToken.getStatusCode(), accessToken.getBody()).getAccessToken();
        } catch (Exception e) {
            dealException(e);
            return null;
        }
    }

    public static String liandanlu() {
        try {
            com.aliyun.dingtalkai_paa_s_1_0.Client client = new Client(CONFIG);
            LiandanluExclusiveModelRequest request = new LiandanluExclusiveModelRequest();
            request.setModule("aiChatData");
            request.setModelId("model-igor-tongji-chatbi-1-wqdr");
            request.setPrompt("近10年杭州市余杭区的财政收入");
            request.setUserId("1");
            LiandanluExclusiveModelHeaders headers = new LiandanluExclusiveModelHeaders();
            headers.setXAcsDingtalkAccessToken("257e2a58e2fe378d810d0388f59472ac");
            LiandanluExclusiveModelResponse response = client.liandanluExclusiveModelWithOptions(request, headers, new com.aliyun.teautil.models.RuntimeOptions());
            Map<String, Map<String, String>> body = (Map<String, Map<String, String>>) getBody(response.getStatusCode(), response.getBody().getResult());
            if (body == null) {
                return null;
            }
            return body.get("result").get("content");
        } catch (Exception e) {
            dealException(e);
            return null;
        }
    }

    private static void dealException(Exception e) {
        TeaException teaException;
        if (e instanceof TeaException) {
            teaException = (TeaException) e;
        } else {
            teaException = new TeaException(e.getMessage(), e);
        }
        if (!com.aliyun.teautil.Common.empty(teaException.code) && !com.aliyun.teautil.Common.empty(teaException.message)) {
            log.error("code:{} message:{}", teaException.code, teaException.message);
        }
    }

    private static <T> T getBody(Integer statusCode, T body) {
        log.info("getBody statusCode:{} body:{}", statusCode, JSONUtil.toJsonStr(body));
        if (statusCode != 200) {
            return null;
        }
        return body;
    }

    public static void main(String[] args) throws Exception {
//        String accessToken = getAccessToken("dingqwati6igezdfkmib", "iEuiuLggX_7cOpH4LwhKj1f_ky5sfgs2eitN74pTDXn0-IWsizNrOinGdwXsIWKR");
        String liandanlu = liandanlu();
        System.out.println("");
    }
}