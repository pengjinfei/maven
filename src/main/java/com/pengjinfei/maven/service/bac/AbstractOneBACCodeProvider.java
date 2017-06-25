package com.pengjinfei.maven.service.bac;

/**
 * Created on 6/15/17
 *
 * @author Pengjinfei
 */
public abstract class AbstractOneBACCodeProvider extends AbstractBatchAsyncCacheableCodeProvider implements OneBACCodeProvider {
    @Override
    public void loadCache(Integer thirdpartyCode) {
        long times = getTimes(thirdpartyCode);
        for (int i = 0; i < times; i++) {
            for (int j = 0; j < getBatchSize(); j++) {
                String s = doGetOne(thirdpartyCode);
                redisTemplate.opsForList().rightPush(getRedisKey(thirdpartyCode), s);
            }
            redisTemplate.opsForList().rightPush(getRedisKey(thirdpartyCode), POSION);
        }
    }
}
