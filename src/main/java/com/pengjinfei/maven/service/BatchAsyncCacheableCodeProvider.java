package com.pengjinfei.maven.service;

/**
 * Created on 6/15/17
 *
 * @author Pengjinfei
 */
public interface BatchAsyncCacheableCodeProvider extends AsyncCacheableCodeProvider {

    int getBatchSize();

    int getBatchNum();

}
