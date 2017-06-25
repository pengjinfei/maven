package com.pengjinfei.maven.service.bac;

/**
 * Created on 6/15/17
 *
 * @author Pengjinfei
 */
public interface OneBACCodeProvider extends BatchAsyncCacheableCodeProvider{

    String doGetOne(Integer thirdpartyCode);
}
