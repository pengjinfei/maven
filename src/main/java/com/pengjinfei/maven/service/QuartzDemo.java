package com.pengjinfei.maven.service;

import com.pengjinfei.maven.configuration.quartz.QuartzJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Created on 8/19/17
 *
 * @author Pengjinfei
 */
@Service
@Slf4j
public class QuartzDemo {

    @QuartzJob(cronExpression = "0/15 * * * * ?")
    public void testQuartz() {
        log.info("I'm running");
    }

    @QuartzJob(cronExpression = "0/20 * * * * ?")
    public void testQuartzJobAnnotation() {
        log.info("Running on annotation");
    }

    @QuartzJob(cronExpression = "0/10 * * * * ?",name = "中文")
    public void testQuartzJobAnnotationWithChinese() {
        log.info("Running on annotation with chinese jod name");
    }
}
