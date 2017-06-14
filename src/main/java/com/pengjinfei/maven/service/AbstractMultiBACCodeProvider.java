package com.pengjinfei.maven.service;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.List;

/**
 * Created on 6/15/17
 *
 * @author Pengjinfei
 */
public abstract class AbstractMultiBACCodeProvider extends AbstractBatchAsyncCacheableCodeProvider implements MultiBACCodeProvider {

    @Override
    public void loadCache() {
        long times = getTimes();
        for (int i = 0; i < times; i++) {
            final List<String> list = doGetMulti();
            list.add(POSION);
            redisTemplate.executePipelined(new RedisCallback<Object>() {
                @Override
                public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                    RedisSerializer<String> stringSerializer = redisTemplate.getStringSerializer();
                    for (String s : list) {
                        redisConnection.rPush(stringSerializer.serialize(getRedisKey()), stringSerializer.serialize(s));
                    }
                    return null;
                }
            });
        }
    }

}

