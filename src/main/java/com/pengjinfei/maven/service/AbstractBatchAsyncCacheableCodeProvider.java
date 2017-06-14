package com.pengjinfei.maven.service;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Created on 6/15/17
 *
 * @author Pengjinfei
 */
public abstract class AbstractBatchAsyncCacheableCodeProvider implements BatchAsyncCacheableCodeProvider,BeanNameAware {

    @Autowired
    protected StringRedisTemplate redisTemplate;

    private String beanName;


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

    protected long getTimes() {
        Long size = redisTemplate.opsForList().size(getRedisKey());
        int batchNum = getBatchNum();
        int batchSize = getBatchSize();
        int maxSize = batchNum * (batchSize + 1);
        return (maxSize - size) / (batchSize + 1);
    }
}
