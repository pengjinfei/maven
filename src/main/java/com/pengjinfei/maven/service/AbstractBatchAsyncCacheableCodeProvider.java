package com.pengjinfei.maven.service;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * Created on 6/15/17
 *
 * @author Pengjinfei
 */
public abstract class AbstractBatchAsyncCacheableCodeProvider implements BatchAsyncCacheableCodeProvider,BeanNameAware {

    @Autowired
    protected StringRedisTemplate redisTemplate;

    private String beanName;

    int BATCH_SIZE = 10;

    int BATCH_NUM = 5;

    String POSION = "I'm posion";


    StringRedisTemplate getRedisTemplate() {
        return this.redisTemplate;
    }

    @Override
    public int getBatchSize() {
        return BATCH_SIZE;
    }

    @Override
    public void setBeanName(String s) {
        this.beanName = s;
    }

    protected String getRedisKey() {
        return "token:" + this.beanName;
    }

    @Override
    public int getBatchNum() {
        return BATCH_NUM;
    }

    @Override
    public String getCodeByCache() {
        return redisTemplate.opsForList().leftPop(getRedisKey());
    }

    @Override
    public String getCodeByCacheBlocked(long time, TimeUnit unit) {
        return redisTemplate.opsForList().leftPop(getRedisKey(), time, unit);
    }

    @Override
    public boolean isPosion(String code) {
        return POSION.equals(code);
    }

    protected long getTimes() {
        Long size = redisTemplate.opsForList().size(getRedisKey());
        int batchNum = getBatchNum();
        int batchSize = getBatchSize();
        int maxSize = batchNum * (batchSize + 1);
        return (maxSize - size) / (batchSize + 1);
    }
}
