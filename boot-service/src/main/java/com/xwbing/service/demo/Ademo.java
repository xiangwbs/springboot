package com.xwbing.service.demo;

import com.aliyun.dingtalkai_paa_s_1_0.Client;
import com.aliyun.dingtalkai_paa_s_1_0.models.LiandanluExclusiveModelHeaders;
import com.aliyun.dingtalkai_paa_s_1_0.models.LiandanluExclusiveModelRequest;
import com.aliyun.dingtalkai_paa_s_1_0.models.LiandanluExclusiveModelResponse;
import com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenResponse;
import lombok.extern.java.Log;

/**
 * 项目名称: boot-module-pro
 * 创建时间: 2018/1/23 14:45
 * 作者: xiangwb
 * 说明: 测试用
 */
@Log
public class Ademo {
    public static void main(String[] args) throws Exception {
        accessToken();
    }

    public static void accessToken() throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config();
        config.protocol = "https";
        config.regionId = "central";
        com.aliyun.dingtalkoauth2_1_0.Client client = new com.aliyun.dingtalkoauth2_1_0.Client(config);
        com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenRequest getAccessTokenRequest = new com.aliyun.dingtalkoauth2_1_0.models.GetAccessTokenRequest()
                .setAppKey("dingqwati6igezdfkmib")
                .setAppSecret("iEuiuLggX_7cOpH4LwhKj1f_ky5sfgs2eitN74pTDXn0-IWsizNrOinGdwXsIWKR");
        GetAccessTokenResponse accessToken = client.getAccessToken(getAccessTokenRequest);
        System.out.println("");
    }

    public static void liandanlu() throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config();
        com.aliyun.dingtalkai_paa_s_1_0.Client client = new Client(config);
        LiandanluExclusiveModelRequest request = new LiandanluExclusiveModelRequest();
        request.setModelId("model-igor-tongji-chatbi-1-wqdr");
        request.setPrompt("OKR是什么");
        LiandanluExclusiveModelHeaders headers = new LiandanluExclusiveModelHeaders();
        headers.setXAcsDingtalkAccessToken("");
        LiandanluExclusiveModelResponse response = client.liandanluExclusiveModelWithOptions(request, headers, new com.aliyun.teautil.models.RuntimeOptions());
    }
}