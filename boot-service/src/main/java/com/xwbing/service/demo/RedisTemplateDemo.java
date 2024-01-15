package com.xwbing.service.demo;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.DataType;
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

    // ---------------------- string ----------------------
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

    // ---------------------- hash ----------------------
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

    // ---------------------- list ----------------------
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

    // ---------------------- set ----------------------
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

    // ---------------------- zset ----------------------
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