package com.pengjinfei.maven.configuration.integration;

/**
 * Created on 8/29/17
 *
 * @author Pengjinfei
 */
public interface RedisScoreCaculator<T> {

    double score(T t);
}
