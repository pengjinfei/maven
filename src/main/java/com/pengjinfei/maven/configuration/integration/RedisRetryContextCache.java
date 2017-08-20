package com.pengjinfei.maven.configuration.integration;

import lombok.Data;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.RetryContext;
import org.springframework.retry.policy.RetryCacheCapacityExceededException;
import org.springframework.retry.policy.RetryContextCache;

import java.util.UUID;

/**
 * Created on 8/20/17
 *
 * @author Pengjinfei
 */
@Data
public class RedisRetryContextCache implements RetryContextCache {

    private RedisTemplate template;

    private String key = "retryContext";

    @Override
    public RetryContext get(Object o) {
        if (o instanceof UUID) {
            UUID id = (UUID) o;
            return (RetryContext) template.opsForHash().get(key, id.toString());
        } else {
            return (RetryContext) template.opsForHash().get(key, o);
        }
    }

    @Override
    public void put(Object o, RetryContext retryContext) throws RetryCacheCapacityExceededException {
        if (o instanceof UUID) {
            UUID id = (UUID) o;
            template.opsForHash().put(key, id.toString(), retryContext);
        } else {
            template.opsForHash().put(key, o, retryContext);
        }
    }

    @Override
    public void remove(Object o) {
        if (o instanceof UUID) {
            UUID id = (UUID) o;
            template.opsForHash().delete(key, id.toString());
        } else {
            template.opsForHash().delete(key, o);
        }
    }

    @Override
    public boolean containsKey(Object o) {
        if (o instanceof UUID) {
            UUID id = (UUID) o;
            return template.opsForHash().hasKey(key, id.toString());
        } else {
            return template.opsForHash().hasKey(key, o);
        }
    }
}
