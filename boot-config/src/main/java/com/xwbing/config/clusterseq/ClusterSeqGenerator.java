package com.xwbing.config.clusterseq;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

import com.xwbing.config.redis.RedisService;

import lombok.extern.slf4j.Slf4j;

/**
 * 集群系列号生成器，用于订单号
 *
 * id按长度16设计。环境（1位）+ 年月日时分（10位）+ 自增序列（{@link #SEQ_SIZE}位）。每次自增在{@link #RANDOM_NEXT_OFFSET}内随机。
 * 一分钟内最少可生成的序列数 = ({@link #MAX_SEQ})/{@link #RANDOM_NEXT_OFFSET}
 * 如果一分钟内{@link #SEQ_SIZE}位的自增序列用完，则等待下一秒再生成
 *
 * @author daofeng
 * @version $
 * @since 2020年01月03日 10:38
 */
@Slf4j
public class ClusterSeqGenerator {
    private static final String KEY_PREFIX = "boot:";
    private final int SEQ_SIZE = 5;
    private final int RANDOM_NEXT_OFFSET = 30;
    private final Long MAX_SEQ = Double.valueOf(Math.pow(10, SEQ_SIZE)).longValue() - 1;
    private final RedisService redisService;
    private final int envType;

    public ClusterSeqGenerator(RedisService redisService, int envType) {
        this.redisService = redisService;
        this.envType = envType;
    }

    /**
     * 获取序列号
     *
     * @param bizType 业务类型 自定义
     *
     * @return
     */
    public Long getSeqId(String bizType) {
        String date = getDateInfo();
        String key = KEY_PREFIX + bizType + date;
        redisService.expire(key, 90);
        Long seq = redisService.incrBy(key, ThreadLocalRandom.current().nextInt(1, RANDOM_NEXT_OFFSET));
        if (seq <= MAX_SEQ) {
            return Long.valueOf(envType + date + String.format("%0" + SEQ_SIZE + "d", seq));
        } else {
            nextDate(date);
            return getSeqId(bizType);
        }
    }

    /**
     * 阻塞到下一分，直到获得新的时间信息
     *
     * @param lastDate
     */
    private void nextDate(String lastDate) {
        long date = Long.valueOf(getDateInfo());
        while (date <= Long.valueOf(lastDate)) {
            date = Long.valueOf(getDateInfo());
        }
    }

    /**
     * 获取时间信息
     *
     * @return
     */
    private String getDateInfo() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMddHHmm");
        return LocalDateTime.now().format(formatter);
    }
}
