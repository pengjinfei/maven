package com.pengjinfei.maven.service;

import org.springframework.scheduling.annotation.Async;

import java.util.concurrent.TimeUnit;

/**
* Created on 6/15/17
* @author Pengjinfei
*/
public interface AsyncCacheableCodeProvider {

    @Async
    void loadCache();

    String getCodeByCache();

    String getCodeByCacheBlocked(long time, TimeUnit unit);

    boolean isPosion(String code);
}
