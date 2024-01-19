package com.xwbing.service.demo;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author daofeng
 * @version $
 * @since 2024年01月15日 11:38 AM
 */
@RequiredArgsConstructor
@Component
public class RedisTemplateDemo {
    private final RedisTemplate<String, Object> redisTemplate;

    public void common() {
        redisTemplate.delete("key");
        redisTemplate.expire("key", 10, TimeUnit.SECONDS);
        redisTemplate.expireAt("key", new Date());
        redisTemplate.getExpire("key", TimeUnit.SECONDS);
        redisTemplate.hasKey("key");
        redisTemplate.keys("key");
        redisTemplate.renameIfAbsent("oldKey", "newKey");
        DataType type = redisTemplate.type("key");
    }

    /**
     * hashMap<string,object>
     * 热点数据缓存，计数场景(数据统计，全局序列，频率控制)，分布式锁
     */
    public void string() {
        redisTemplate.opsForValue().set("stringKey", "value");
        redisTemplate.opsForValue().set("stringKey", "value", 10, TimeUnit.SECONDS);
        redisTemplate.opsForValue().setIfAbsent("stringKey", "value");
        redisTemplate.opsForValue().get("stringKey");
        redisTemplate.opsForValue().getAndSet("stringKey", "value");
        redisTemplate.opsForValue().multiGet(Collections.emptyList());
        redisTemplate.opsForValue().append("stringKey", "suffix");
        redisTemplate.opsForValue().increment("stringKey");
    }

    /**
     * 可以容纳最少2^32(4个字节) 。可以想象成一个数组，数组的下标即是offset，数组只能存储0|1
     * 布隆过滤器，统计活跃用户，统计用户是否在线，签到(数据量大)
     */
    public void bit() {
        redisTemplate.opsForValue().setBit("visit_23_01_01", 25255, true);
        redisTemplate.opsForValue().setBit("visit_23_01_01", 25250, true);
        redisTemplate.opsForValue().setBit("visit_23_01_02", 25255, true);
        Boolean sign230101 = redisTemplate.opsForValue().getBit("visit_23_01_01", 25255);
        Long count = redisTemplate.execute((RedisCallback<Long>) connection -> connection.bitCount("visit_23_01_01".getBytes()));
        // 连续访问用户
        redisTemplate.execute((RedisCallback<Long>) connection -> connection.bitOp(RedisStringCommands.BitOperation.AND, "visit_23_01_continue".getBytes(), "visit_23_01_01".getBytes(), "visit_23_01_02".getBytes()));
        // 所有访问用户
        redisTemplate.execute((RedisCallback<Long>) connection -> connection.bitOp(RedisStringCommands.BitOperation.OR, "visit_23_01_all".getBytes(), "visit_23_01_01".getBytes(), "visit_23_01_02".getBytes()));

//        //连续签到
//        redisTemplate.opsForValue().setBit("sign_23_user_1", 1, true);
//        redisTemplate.opsForValue().setBit("sign_23_user_1", 2, true);
//        redisTemplate.execute((RedisCallback<Long>) connection -> {
//            connection.bitField("sign_23_user_1".getBytes(), BitFieldSubCommands.create().get(BitFieldSubCommands.BitFieldType.signed()).valueAt());
//        })
    }

    /**
     * hashMap<string,hashMap<string,object>>
     * 对象型数据，pu，uv
     */
    public void hash() {
        redisTemplate.opsForHash().put("hkey", "hashKey", "value");
        redisTemplate.opsForHash().putIfAbsent("hkey", "hashKey", "value");
        redisTemplate.opsForHash().putAll("hkey", Collections.emptyMap());
        redisTemplate.opsForHash().delete("hkey", "hashKey");
        String value = (String) redisTemplate.opsForHash().get("key", "hashKey");
        redisTemplate.opsForHash().hasKey("hkey", "hashKey");
        redisTemplate.opsForHash().size("hkey");
        redisTemplate.opsForHash().entries("hkey");
        redisTemplate.opsForHash().keys("hkey");
        redisTemplate.opsForHash().values("hkey");
        redisTemplate.opsForHash().increment("hkey", "hashKey", 1L);
    }

    /**
     * hashMap<string,linkedList<object>>
     * 时间线列表(朋友圈)，队列，商品秒杀
     */
    public void list() {
        redisTemplate.opsForList().rightPush("listKey", "value");
        redisTemplate.opsForList().rightPushIfPresent("listKey", "value");
        redisTemplate.opsForList().rightPushAll("listKey", Collections.emptyList());
        redisTemplate.opsForList().leftPop("listKey");
        redisTemplate.opsForList().remove("listKey", 0, "value");
        redisTemplate.opsForList().index("listKey", 0);
        redisTemplate.opsForList().range("listKey", 0, -1);
        redisTemplate.opsForList().size("listKey");
    }

    /**
     * hashMap<string,hashSet>
     * 抽奖，点赞|签到(数据量少)，共同关注(交集)，可能认识的人(差集)
     */
    public void set() {
        redisTemplate.opsForSet().add("setKey", "value");
        // 随机删除元素并返回
        Object value = redisTemplate.opsForSet().pop("setKey");
        // 随机获取元素
        value = redisTemplate.opsForSet().randomMember("setKey");
        redisTemplate.opsForSet().randomMembers("setKey", 2);
        redisTemplate.opsForSet().distinctRandomMembers("setKey", 2);
        redisTemplate.opsForSet().size("setKey");
        redisTemplate.opsForSet().isMember("setKey", "value");
        redisTemplate.opsForSet().members("setKey");
        redisTemplate.opsForSet().remove("setKey", "value");

        // 交集
        redisTemplate.opsForSet().intersect("setKey", "otherSetKey");
        redisTemplate.opsForSet().intersectAndStore("setKey", "otherSetKey", "newSetKey");
        // 并集
        redisTemplate.opsForSet().union("setKey", "otherSetKey");
        redisTemplate.opsForSet().unionAndStore("setKey", "otherSetKey", "newSetKey");
        // 差集
        redisTemplate.opsForSet().difference("setKey", "otherSetKey");
        redisTemplate.opsForSet().differenceAndStore("setKey", "otherSetKey", "newSetKey");
    }

    /**
     * hashMap<string,sortedSet>
     * 排行榜，定时任务
     */
    public void zset() {
        redisTemplate.opsForZSet().add("zsetKey", "value", 60);
        redisTemplate.opsForZSet().incrementScore("zsetKey", "value", 5);
        redisTemplate.opsForZSet().score("zsetKey", "value");
        redisTemplate.opsForZSet().size("zsetKey");
        redisTemplate.opsForZSet().count("zsetKey", 90, 100);
        redisTemplate.opsForZSet().remove("zsetKey", "value");
        redisTemplate.opsForZSet().removeRange("zsetKey", 0, 10);
        redisTemplate.opsForZSet().removeRangeByScore("zsetKey", 0, 60);

        // 从小到大的排名
        Long rank = redisTemplate.opsForZSet().rank("zsetKey", "value");
        // 从大到小的排名
        rank = redisTemplate.opsForZSet().reverseRank("zsetKey", "value");

        // 倒数10名
        redisTemplate.opsForZSet().rangeWithScores("zsetKey", 0, 10);
        // 前10名
        redisTemplate.opsForZSet().reverseRangeWithScores("zsetKey", 0, 10);

        redisTemplate.opsForZSet().rangeByScoreWithScores("zsetKey", 80, 90, 0, 10);
        redisTemplate.opsForZSet().reverseRangeByScoreWithScores("zsetKey", 80, 90, 0, 10);

        // 并集
        redisTemplate.opsForZSet().unionAndStore("zsetKey", "otherZsetKey", "newZsetKey");
        // 交集
        redisTemplate.opsForZSet().intersectAndStore("zsetKey", "otherZsetKey", "newZsetKey");
    }
}