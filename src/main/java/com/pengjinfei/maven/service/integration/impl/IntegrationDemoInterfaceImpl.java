package com.pengjinfei.maven.service.integration.impl;

import com.pengjinfei.maven.configuration.integration.AsynRunner;
import com.pengjinfei.maven.entity.Person;
import com.pengjinfei.maven.service.integration.IntegrationDemoInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Created on 8/21/17
 *
 * @author Pengjinfei
 */
@Service("integrationDemoInterface")
@Slf4j
public class IntegrationDemoInterfaceImpl implements IntegrationDemoInterface {

    @Override
    @AsynRunner(cron = "0/10 1 * * * ?",
            retryCron="0/10 1 * * * ?",
            delayedTime = 20,
            timeUnit = TimeUnit.SECONDS,
            insertSqlId = "com.pengjinfei.maven.mapper.PersonMapper.insertSelective",
            failedSqlId = "com.pengjinfei.maven.mapper.PersonMapper.updateAddress")
    public void sayHello(Person person) {
        log.info("I get you {}!",person);
        throw new RuntimeException("Test Asyn Annotation");
    }
}
