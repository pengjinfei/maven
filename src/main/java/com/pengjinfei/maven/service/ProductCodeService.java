package com.pengjinfei.maven.service;

import com.pengjinfei.maven.dto.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Created on 6/15/17
 *
 * @author Pengjinfei
 */
@Service
@Slf4j
public class ProductCodeService implements ApplicationContextAware{

    @Autowired
    private ApplicationContext applicationContext;

    public String getCode(Product product) {
        AsyncCacheableCodeProvider asyncCacheableCodeProvider = applicationContext.getBean(product.getCodeProvider(), AsyncCacheableCodeProvider.class);
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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
