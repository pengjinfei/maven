package com.pengjinfei.maven.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Created on 6/15/17
 *
 * @author Pengjinfei
 */
@Service
@Slf4j
public class ProductCodeService {

    @Autowired
    AsyncCacheableCodeProvider asyncCacheableCodeProvider;

    public String getCode() {
        String codeByCache = asyncCacheableCodeProvider.getCodeByCache();
        if (codeByCache == null) {
            asyncCacheableCodeProvider.loadCache();
            return asyncCacheableCodeProvider.getCodeByCacheBlocked(3, TimeUnit.SECONDS);
        }
        if (asyncCacheableCodeProvider.isPosion(codeByCache)) {
            asyncCacheableCodeProvider.loadCache();
            return asyncCacheableCodeProvider.getCodeByCache();
        }
        return codeByCache;
    }
}
