package com.pengjinfei.maven.service.cache;

import com.pengjinfei.maven.configuration.integration.AsynRunner;
import com.pengjinfei.maven.entity.Person;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Created on 9/6/17
 *
 * @author Pengjinfei
 */
@Service("cacheServie")
public class CacheServiceImpl implements CacheService {

    @Override
    @Cacheable(key = "#root.methodName",cacheNames = "abc")
    public Person getPerson() {
        Person p =new Person();
        p.setName("pjf");
        p.setAddress("shenzhen");
        return p;    }

    @Override
    @AsynRunner(cron = "0/12 * * * * ?",
            retryCron="0 0/1 * * * ?",
            delayedTime = 20,
            timeUnit = TimeUnit.SECONDS,
            insertSqlId = "com.pengjinfei.maven.mapper.PersonMapper.insertSelective",
            failedSqlId = "com.pengjinfei.maven.mapper.PersonMapper.updateAddress")
    public void testCache(Person person) {
        System.out.println("good");
    }
}
