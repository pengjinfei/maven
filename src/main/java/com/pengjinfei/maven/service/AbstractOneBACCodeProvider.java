package com.pengjinfei.maven.service;

/**
 * Created on 6/15/17
 *
 * @author Pengjinfei
 */
public abstract class AbstractOneBACCodeProvider extends AbstractBatchAsyncCacheableCodeProvider implements OneBACCodeProvider {
    @Override
    public void loadCache() {
        for (int i = 0; i < getTimes(); i++) {
            for (int j = 0; j < getBatchSize(); j++) {
                String s = doGetOne();
                redisTemplate.opsForList().rightPush(getRedisKey(), s);
            }
            redisTemplate.opsForList().rightPush(getRedisKey(), POSION);
        }
    }
}
