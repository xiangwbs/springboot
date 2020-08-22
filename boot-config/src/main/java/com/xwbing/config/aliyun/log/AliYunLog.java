package com.xwbing.config.aliyun.log;

import java.util.Date;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.LogItem;
import com.aliyun.openservices.log.request.PutLogsRequest;

import lombok.extern.slf4j.Slf4j;

/**
 * @author xiangwb
 *         aliyunlog dingtalk
 */
@Slf4j
public class AliYunLog {
    private static final String HOST = System.getenv("hostName");
    private Client client;
    private String logStore;
    private String topic;
    private String project;

    public AliYunLog(Client client, String logStore, String topic, String project) {
        this.client = client;
        this.project = project;
        this.logStore = logStore;
        this.topic = topic;
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
        LogItem logItem = new LogItem((int)((new Date()).getTime() / 1000L));
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