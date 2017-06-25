package com.pengjinfei.maven.service.bac;

import org.springframework.scheduling.annotation.Async;

import java.util.concurrent.TimeUnit;

/**
* Created on 6/15/17
* @author Pengjinfei
*/
public interface AsyncCacheableCodeProvider {

    @Async
    void loadCache(Integer thirdpartyCode);

    String getCodeByCache(Integer thirdpartyCode);

    String getCodeByCacheBlocked(Integer thirdpartyCode, TimeUnit unit, long time);

    boolean isPosion(String code);
}
