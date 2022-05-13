package com.xwbing.starter.yunxin;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.alibaba.fastjson.JSONObject;
import com.xwbing.starter.exception.ConfigException;
import com.xwbing.starter.yunxin.util.YunXinUtil;
import com.xwbing.starter.yunxin.vo.YunXinAccountVO;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author daofeng
 * @version $Id$
 * @since 2021年12月27日 10:21 AM
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(YunXinProperties.class)
public class YunXinHelper {
    private static final String BASE_URL = "https://api.netease.im/nimserver";
    private static final String USER_CREATE = BASE_URL + "/user/create.action";
    private static final String CHATROOM_CREATE = BASE_URL + "/chatroom/create.action";
    private static final String CHATROOM_TOGGLE_CLOSE_STAT = BASE_URL + "/chatroom/toggleCloseStat.action";
    private final YunXinProperties properties;

    public YunXinHelper(YunXinProperties properties) {
        this.properties = properties;
    }

    /**
     * 创建网易云信IM账号
     *
     * @param accId 网易云信IM账号
     * @param name 网易云信IM账号昵称
     */
    public YunXinAccountVO createAccId(String accId, String name) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost post = post(USER_CREATE);
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("accid", accId));
        nameValuePairs.add(new BasicNameValuePair("name", name));
        nameValuePairs.add(new BasicNameValuePair("token", Base64.encode(accId)));
        try {
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));
            HttpResponse response = httpclient.execute(post);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String entityStr = EntityUtils.toString(entity, "UTF-8");
                    log.info("createAccId accId:{} entityStr:{}", accId, entityStr);
                    JSONObject result = JSONObject.parseObject(entityStr);
                    if (result.getLong("code") == HttpStatus.SC_OK) {
                        return YunXinAccountVO.builder().accId(accId).token(Base64.encode(accId)).build();
                    }
                }
            }
            log.error("createAccId accId:{} no result", accId);
            throw new ConfigException("创建im账号失败");
        } catch (Exception e) {
            log.error("createAccId accId:{} error", accId, e);
            throw new ConfigException("创建im账号失败");
        }
    }

    /**
     * 创建聊天室
     *
     * @param accId 聊天室属主的账号accid
     * @param name 聊天室名称
     */
    public Long createChatroom(String accId, String name) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost post = post(CHATROOM_CREATE);
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("creator", accId));
        nameValuePairs.add(new BasicNameValuePair("name", name));
        try {
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));
            HttpResponse response = httpclient.execute(post);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String entityStr = EntityUtils.toString(entity, "UTF-8");
                    log.info("createChatroom accId:{} entityStr:{}", accId, entityStr);
                    JSONObject result = JSONObject.parseObject(entityStr);
                    if (result.getLong("code") == HttpStatus.SC_OK) {
                        return result.getJSONObject("chatroom").getLong("roomid");
                    }
                }
            }
            log.error("createChatroom accId:{} no result", accId);
            throw new ConfigException("创建聊天室状态失败");
        } catch (Exception e) {
            log.error("createChatroom accId:{} error", accId, e);
            throw new ConfigException("创建聊天室状态失败");
        }
    }

    /**
     * 关闭聊天室
     *
     * @param accId 创建者的账号accid
     * @param roomId 聊天室id
     */
    public void closeChatroom(String accId, Long roomId) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost post = post(CHATROOM_TOGGLE_CLOSE_STAT);
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("roomid", String.valueOf(roomId)));
        nameValuePairs.add(new BasicNameValuePair("operator", accId));
        nameValuePairs.add(new BasicNameValuePair("valid", Boolean.FALSE.toString()));
        try {
            post.setEntity(new UrlEncodedFormEntity(nameValuePairs, "utf-8"));
            HttpResponse response = httpclient.execute(post);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String entityStr = EntityUtils.toString(entity, "UTF-8");
                    log.info("toggleCloseStatChatroom roomId:{} entityStr:{}", roomId, entityStr);
                    JSONObject result = JSONObject.parseObject(entityStr);
                    if (result.getLong("code") == HttpStatus.SC_OK) {
                        return;
                    }
                }
            }
            log.error("toggleCloseStatChatroom roomId:{} no result", roomId);
            throw new ConfigException("修改聊天室状态失败");
        } catch (Exception e) {
            log.error("toggleCloseStatChatroom roomId:{} error", roomId, e);
            throw new ConfigException("修改聊天室状态失败");
        }
    }

    private HttpPost post(String url) {
        HttpPost post = new HttpPost(url);
        String curTime = String.valueOf((System.currentTimeMillis() / 1000L));
        String nonce = RandomUtil.randomString(10);
        String checkSum = YunXinUtil.getCheckSum(properties.getAppSecret(), nonce, curTime);
        post.addHeader("AppKey", properties.getAppKey());
        post.addHeader("Nonce", nonce);
        post.addHeader("CurTime", curTime);
        post.addHeader("CheckSum", checkSum);
        post.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        return post;
    }
}
