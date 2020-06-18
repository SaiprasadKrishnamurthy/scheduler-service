package com.fp.scheduler.config;

import com.github.sonus21.rqueue.spring.EnableRqueue;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;

/**
 * Redis configurations.
 *
 * @author Sai.
 */
@Data
@Configuration
@EnableRqueue
public class RedisConfig {

    @Autowired
    private Environment environment;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(environment.getProperty("redis.host"));
        redisStandaloneConfiguration.setPort(environment.getProperty("redis.port", Integer.class));
        redisStandaloneConfiguration.setDatabase(0);
        redisStandaloneConfiguration.setPassword(RedisPassword.of(environment.getProperty("redis.password")));
        JedisClientConfiguration.JedisClientConfigurationBuilder jedisClientConfiguration = JedisClientConfiguration.builder();
        jedisClientConfiguration.connectTimeout(Duration.ofSeconds(environment.getProperty("redis.connection.timeout.seconds", Integer.class)));
        jedisClientConfiguration.usePooling().poolConfig(buildPoolConfig());
        return new JedisConnectionFactory(redisStandaloneConfiguration, jedisClientConfiguration.build());
    }

    private JedisPoolConfig buildPoolConfig() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(environment.getProperty("redis.connection.pool.max.total", Integer.class));
        poolConfig.setMaxIdle(environment.getProperty("redis.connection.pool.max.idle", Integer.class));
        poolConfig.setTestOnBorrow(environment.getProperty("redis.connection.pool.test.on.borrow", Boolean.class));
        poolConfig.setTestOnReturn(environment.getProperty("redis.connection.pool.test.on.return", Boolean.class));
        poolConfig.setTestWhileIdle(environment.getProperty("redis.connection.pool.test.on.idle", Boolean.class));
        poolConfig.setMinEvictableIdleTimeMillis(Duration.ofSeconds(environment.getProperty("redis.connection.pool.min.evictable.idle.time.seconds", Integer.class)).toMillis());
        poolConfig.setTimeBetweenEvictionRunsMillis(Duration.ofSeconds(environment.getProperty("redis.connection.pool.time.between.eviction.runs.seconds", Integer.class)).toMillis());
        poolConfig.setBlockWhenExhausted(environment.getProperty("redis.connection.pool.block.when.exhausted", Boolean.class));
        poolConfig.setTestOnCreate(environment.getProperty("redis.connection.pool.test.on.create", Boolean.class));
        return poolConfig;
    }
}
