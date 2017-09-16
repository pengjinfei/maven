package com.pengjinfei.maven.batch;

import java.io.Serializable;
import java.util.List;

/**
 * Created on 9/16/17
 *
 * @author Pengjinfei
 */
public interface BatchRetryProcessor<T extends Serializable> {

    BatchRetryResult<T> process(List<T> data);

    void onFail(List<ErrorCarrier<T>> failList);

    void send(T t);

    void send(List<T> list);
}
