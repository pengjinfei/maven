package com.pengjinfei.maven.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

/**
 * Created on 6/15/17
 *
 * @author Pengjinfei
 */
@Service
public class OneSleepBACCodeProvider extends AbstractOneBACCodeProvider {
    @Override
    public String doGetOne() {
        String s = RandomStringUtils.randomAlphabetic(10);
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return s;
    }
}
