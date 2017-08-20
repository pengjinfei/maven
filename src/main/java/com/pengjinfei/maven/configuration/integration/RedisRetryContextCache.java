package com.pengjinfei.maven.configuration.integration;

import lombok.Data;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.RetryContext;
import org.springframework.retry.policy.RetryCacheCapacityExceededException;
import org.springframework.retry.policy.RetryContextCache;

/**
 * Created on 8/20/17
 *
 * @author Pengjinfei
 */
@Data
@SuppressWarnings("unchecked")
public class RedisRetryContextCache implements RetryContextCache {

    private RedisTemplate template;

    private String key = "retryContext";

    @Override
    public RetryContext get(Object o) {
        return (RetryContext) template.opsForHash().get(key, o);
    }

    @Override
    public void put(Object o, RetryContext retryContext) throws RetryCacheCapacityExceededException {
        /*
          retryContext的异常信息非常多,影响序列化效率
          可以考虑将其置为 null,或者替换为其他简略信息(时间戳,线程 UUID 等)
         */
/*        if (retryContext instanceof RetryContextSupport) {
            RetryContextSupport context = (RetryContextSupport) retryContext;
            context.registerThrowable(null);
        }*/
        template.opsForHash().put(key, o, retryContext);
    }

    @Override
    public void remove(Object o) {
        template.opsForHash().delete(key, o);
    }

    @Override
    public boolean containsKey(Object o) {
        return template.opsForHash().hasKey(key, o);
    }
}
