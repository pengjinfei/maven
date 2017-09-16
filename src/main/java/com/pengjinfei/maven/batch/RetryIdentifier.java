package com.pengjinfei.maven.batch;

/**
 * Created on 9/16/17
 *
 * @author Pengjinfei
 */
public interface RetryIdentifier<T> {

    String identify(T t);
}
