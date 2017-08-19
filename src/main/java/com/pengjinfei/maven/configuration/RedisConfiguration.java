package com.pengjinfei.maven.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Created on 8/13/17
 *
 * @author Pengjinfei
 */
@Configuration
public class RedisConfiguration {
    @Bean
    public RedisTemplate template(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate redisTemplate=new RedisTemplate();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new FstSerializer());
        return redisTemplate;
    }
}
