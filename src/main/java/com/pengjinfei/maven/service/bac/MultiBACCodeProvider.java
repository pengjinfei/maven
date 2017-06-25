package com.pengjinfei.maven.service.bac;

import java.util.List;

/**
 * Created on 6/15/17
 *
 * @author Pengjinfei
 */
public interface MultiBACCodeProvider extends BatchAsyncCacheableCodeProvider{

    List<String> doGetMulti(Integer thirdpartyCode);
}
