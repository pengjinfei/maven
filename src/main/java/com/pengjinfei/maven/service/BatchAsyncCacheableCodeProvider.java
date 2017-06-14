package com.pengjinfei.maven.service;

/**
 * Created on 6/15/17
 *
 * @author Pengjinfei
 */
public interface BatchAsyncCacheableCodeProvider extends AsyncCacheableCodeProvider {

    int BATCH_SIZE = 100;

    int BATCH_NUM = 5;

    String POSION = "I'm posion";

    int getBatchSize();

    int getBatchNum();

}
