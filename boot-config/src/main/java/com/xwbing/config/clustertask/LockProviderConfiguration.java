package com.xwbing.config.clustertask;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.redis.spring.RedisLockProvider;

/**
 * @author daofeng
 * @version $
 * @since 2020年08月16日 15:49
 */
@Configuration
public class LockProviderConfiguration {

    @Bean
    public LockProvider lockProvider(RedisConnectionFactory connectionFactory, Environment environment) {
        String activeProfile = environment.getActiveProfiles()[0];
        return new RedisLockProvider(connectionFactory, activeProfile);
    }
}