package com.pengjinfei.maven.service.bac;

import com.pengjinfei.maven.entity.ThirdPartyProduct;
import com.pengjinfei.maven.service.bac.AsyncCacheableCodeProvider;
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

    public String getCode(ThirdPartyProduct product) {
        AsyncCacheableCodeProvider asyncCacheableCodeProvider = applicationContext.getBean(product.getCodeServiceName(), AsyncCacheableCodeProvider.class);
        Integer thirdpartyCode = product.getThirdpartyCode();
        String codeByCache = asyncCacheableCodeProvider.getCodeByCache(product.getThirdpartyCode());
        if (codeByCache == null) {
            asyncCacheableCodeProvider.loadCache(product.getThirdpartyCode());
            return asyncCacheableCodeProvider.getCodeByCacheBlocked(thirdpartyCode, TimeUnit.SECONDS, 3);
        }
        if (asyncCacheableCodeProvider.isPosion(codeByCache)) {
            asyncCacheableCodeProvider.loadCache(thirdpartyCode);
            return asyncCacheableCodeProvider.getCodeByCache(thirdpartyCode);
        }
        return codeByCache;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
