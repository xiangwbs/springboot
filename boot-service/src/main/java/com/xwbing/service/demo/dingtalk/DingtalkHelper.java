package com.xwbing.service.demo.dingtalk;

import cn.hutool.json.JSONUtil;
import com.aliyun.dingtalkai_paa_s_1_0.Client;
import com.aliyun.dingtalkai_paa_s_1_0.models.LiandanluExclusiveModelHeaders;
import com.aliyun.dingtalkai_paa_s_1_0.models.LiandanluExclusiveModelRequest;
import com.aliyun.dingtalkai_paa_s_1_0.models.LiandanluExclusiveModelResponse;
import com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenResponse;
import com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenResponseBody;
import com.aliyun.tea.TeaException;
import com.aliyun.teaopenapi.models.Config;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 钉钉新版服务端sdk
 *
 * @author daofeng
 * @version $
 * @since 2024年02月02日 4:59 PM
 */
@RequiredArgsConstructor
@Component
@Slf4j
public class DingtalkHelper {
    private static final String ACCESS_TOKEN_CACHE_KEY = "dingtalk:accessToken";
    private static final Config CONFIG;
    private final StringRedisTemplate stringRedisTemplate;

    static {
        CONFIG = new Config();
        CONFIG.protocol = "https";
        CONFIG.regionId = "central";
    }

    public String getAccessTokenCache(String appKey, String appSecret) {
        String accessToken = stringRedisTemplate.opsForValue().get(ACCESS_TOKEN_CACHE_KEY + appKey);
        if (StringUtils.isNotEmpty(accessToken)) {
            return accessToken;
        }
        accessToken = this.getAccessToken(appKey, appSecret);
        if (StringUtils.isEmpty(accessToken)) {
            return null;
        }
        stringRedisTemplate.opsForValue().set(ACCESS_TOKEN_CACHE_KEY + appKey, accessToken, 100, TimeUnit.MINUTES);
        return accessToken;
    }

    public String getAccessToken(String appKey, String appSecret) {
        try {
            com.aliyun.dingtalkoauth2_1_0.Client client = new com.aliyun.dingtalkoauth2_1_0.Client(CONFIG);
            com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenRequest getAccessTokenRequest = new com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenRequest()
                    .setAppKey(appKey)
                    .setAppSecret(appSecret);
            GetAccessTokenResponse response = client.getAccessToken(getAccessTokenRequest);
            GetAccessTokenResponseBody body = getBody(response.getStatusCode(), response.getBody());
            if (body == null) {
                return null;
            }
            return body.getAccessToken();
        } catch (Exception e) {
            dealException(e);
            return null;
        }
    }

    public String liandanlu(String accessToken, LiandanluExclusiveModelRequest request) {
        try {
            com.aliyun.dingtalkai_paa_s_1_0.Client client = new Client(CONFIG);
//            LiandanluExclusiveModelRequest request = new LiandanluExclusiveModelRequest();
//            request.setModule("aiChatData");
//            request.setModelId("model-igor-tongji-chatbi-1-wqdr");
//            request.setPrompt("近10年杭州市余杭区的财政收入");
//            request.setUserId("1");
            LiandanluExclusiveModelHeaders headers = new LiandanluExclusiveModelHeaders();
            headers.setXAcsDingtalkAccessToken(accessToken);
            LiandanluExclusiveModelResponse response = client.liandanluExclusiveModelWithOptions(request, headers, new com.aliyun.teautil.models.RuntimeOptions());
            Map<String, Map<String, String>> body = (Map<String, Map<String, String>>) getBody(response.getStatusCode(), response.getBody().getResult());
            if (MapUtils.isEmpty(body)) {
                return null;
            }
            return body.get("result").get("content");
        } catch (Exception e) {
            dealException(e);
            return null;
        }
    }

    private void dealException(Exception e) {
        TeaException teaException;
        if (e instanceof TeaException) {
            teaException = (TeaException) e;
        } else {
            teaException = new TeaException(e.getMessage(), e);
        }
        if (!com.aliyun.teautil.Common.empty(teaException.code) && !com.aliyun.teautil.Common.empty(teaException.message)) {
            log.error("dingtalk code:{} message:{}", teaException.code, teaException.message);
        }
    }

    private <T> T getBody(Integer statusCode, T body) {
        log.info("dingtalk getBody statusCode:{} body:{}", statusCode, JSONUtil.toJsonStr(body));
        if (statusCode != 200) {
            return null;
        }
        return body;
    }
}