package com.xwbing.service.demo.dingtalk;

import com.alibaba.fastjson.JSON;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.taobao.api.ApiException;

import lombok.extern.slf4j.Slf4j;

/**
 * 钉钉旧版服务端sdk
 *
 * @author daofeng
 * @version $Id$
 * @since 2023年01月31日 2:18 PM
 */
@Slf4j
public class DingtalkServiceHelper {
    /**
     * accessToken的有效期为7200秒（2小时），有效期内重复获取会返回相同结果并自动续期，过期后获取会返回新的accessToken
     *
     * @param appKey
     * @param appSecret
     */
    public static String getAccessToken(String appKey, String appSecret) {
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/gettoken");
        OapiGettokenRequest request = new OapiGettokenRequest();
        request.setAppkey(appKey);
        request.setAppsecret(appSecret);
        request.setHttpMethod("GET");
        try {
            OapiGettokenResponse response = client.execute(request);
            log.info("dingtalkHelper getAccessToken response:{}", JSON.toJSONString(response));
            return response.getAccessToken();
        } catch (ApiException e) {
            log.error("dingtalkHelper getAccessToken error", e);
            return null;
        }
    }
}