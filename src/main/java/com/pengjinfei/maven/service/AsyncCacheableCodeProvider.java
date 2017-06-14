package com.pengjinfei.maven.service;

import org.springframework.scheduling.annotation.Async;

/**
* Created on 6/15/17
* @author Pengjinfei
*/
public interface AsyncCacheableCodeProvider {

    @Async
    void loadCache();
}
