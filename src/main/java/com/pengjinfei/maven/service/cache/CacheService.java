package com.pengjinfei.maven.service.cache;

import com.pengjinfei.maven.configuration.integration.AsynRunner;
import com.pengjinfei.maven.entity.Person;

import java.util.concurrent.TimeUnit;

/**
 * Created on 9/6/17
 *
 * @author Pengjinfei
 */
public interface CacheService {

    Person getPerson();

    @AsynRunner(cron = "0/12 * * * * ?",
            retryCron="0 0/1 * * * ?",
            delayedTime = 20,
            timeUnit = TimeUnit.SECONDS,
            insertSqlId = "com.pengjinfei.maven.mapper.PersonMapper.insertSelective",
            failedSqlId = "com.pengjinfei.maven.mapper.PersonMapper.updateAddress")
    void testCache(Person person);
}
