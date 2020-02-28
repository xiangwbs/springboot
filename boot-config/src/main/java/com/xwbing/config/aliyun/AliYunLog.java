package com.xwbing.config.aliyun;

import com.alibaba.fastjson.JSONObject;
import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.LogItem;
import com.aliyun.openservices.log.request.PutLogsRequest;
import com.xwbing.config.util.dingTalk.DingTalkClient;
import com.xwbing.config.util.dingTalk.MarkdownMessage;
import com.xwbing.config.util.dingTalk.SendResult;
import com.xwbing.config.util.dingTalk.TextMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.Date;
import java.util.List;
import java.util.Vector;

/**
 * @author xiangwb
 * aliyunlog dingtalk
 */
@Slf4j
public class AliYunLog {
    private static final String HOST = System.getenv("hostName");
    private DingTalkClient dingTalkClient;
    private Client client;
    private String logStore;
    private String topic;
    private String project;
    private String webHook;
    private String secret;

    public AliYunLog(Client client, String logStore, String topic, String project, String webHook, String secret) {
        this.dingTalkClient = new DingTalkClient();
        this.client = client;
        this.project = project;
        this.logStore = logStore;
        this.topic = topic;
        this.webHook = webHook;
        this.secret = secret;
    }

    /**
     * 打印info
     *
     * @param source
     * @param key
     * @param value
     */
    public void info(String source, String key, String value) {
        write(source, key, value, true);
    }

    /**
     * 打印error
     *
     * @param source
     * @param key
     * @param value
     */
    public void error(String source, String key, String value) {
        write(source, key, value, false);
    }

    /**
     * 钉钉群发送text
     *
     * @param source
     * @param atAll
     * @param atMobiles
     * @param params
     */
    public void sendTextMessage(String source, boolean atAll, List<String> atMobiles, Object... params) {
        StringBuilder content = new StringBuilder("host: ").append(HOST).append("\n").append("source: ").append(source).append("\n");
        int i = 1;
        for (Object obj : params) {
            content.append("params").append(i).append(": ").append(obj).append("\n");
            i++;
        }
        try {
            TextMessage textMessage = new TextMessage(content.toString());
            textMessage.addAtMobiles(atMobiles);
            textMessage.setAtAll(atAll);
            SendResult send = dingTalkClient.send(webHook, secret, textMessage);
            if (!send.isSuccess()) {
                log.error("{} - {}", source, JSONObject.toJSON(send));
            }
        } catch (Exception e) {
            log.error("{} - {}", source, ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 钉钉群发送markdown
     *
     * @param markdownMessage
     */
    public void sendMarkdownMessage(MarkdownMessage markdownMessage) {
        try {
            //title当做一级标题
            markdownMessage.addItem(0, MarkdownMessage.getHeaderText(1, markdownMessage.getTitle()));
            SendResult send = dingTalkClient.send(webHook, secret, markdownMessage);
            if (!send.isSuccess()) {
                log.error("{} - {}", markdownMessage.getTitle(), JSONObject.toJSON(send));
            }
        } catch (Exception e) {
            log.error("{} - {}", markdownMessage.getTitle(), ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 打印log
     *
     * @param source
     * @param key
     * @param value
     */
    private void write(String source, String key, String value, boolean info) {
        if (StringUtils.isEmpty(key)) {
            key = "default";
        }
        Vector<LogItem> logGroup = new Vector<>();
        LogItem logItem = new LogItem((int) ((new Date()).getTime() / 1000L));
        logItem.PushBack(key, HOST + "_: " + value);
        logGroup.add(logItem);
        PutLogsRequest putLogsRequest = new PutLogsRequest(project, logStore, topic, source, logGroup);
        try {
            client.PutLogs(putLogsRequest);
            if (info) {
                log.info("{} - {} - {}", source, key, value);
            } else {
                log.error("{} - {} - {}", source, key, value);
            }
        } catch (Exception e) {
            log.error("{} - {}", key, ExceptionUtils.getStackTrace(e));
        }
    }
}