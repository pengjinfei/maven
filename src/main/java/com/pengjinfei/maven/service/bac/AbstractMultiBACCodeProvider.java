package com.pengjinfei.maven.service.bac;

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
    public void loadCache(final Integer thirdpartyCode) {
        long times = getTimes(thirdpartyCode);
        for (int i = 0; i < times; i++) {
            final List<String> list = doGetMulti(thirdpartyCode);
            list.add(POSION);
            redisTemplate.executePipelined(new RedisCallback<Object>() {
                @Override
                public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                    RedisSerializer<String> stringSerializer = redisTemplate.getStringSerializer();
                    for (String s : list) {
                        redisConnection.rPush(stringSerializer.serialize(getRedisKey(thirdpartyCode)), stringSerializer.serialize(s));
                    }
                    return null;
                }
            });
        }
    }

}

