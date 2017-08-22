package com.pengjinfei.maven.service.integration.impl;

import com.pengjinfei.maven.configuration.integration.AsynRunner;
import com.pengjinfei.maven.dto.Person;
import com.pengjinfei.maven.service.integration.IntegrationDemoInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Created on 8/21/17
 *
 * @author Pengjinfei
 */
@Service
@Slf4j
public class IntegrationDemoInterfaceImpl implements IntegrationDemoInterface {

    @Override
    @AsynRunner(cron = "0/10 * * * * ?",retryCron="0/10 * * * * ?")
    public void sayHello(Person person) {
        log.info("I get you {}!",person);
        throw new RuntimeException("Test Asyn Annotation");
    }
}
