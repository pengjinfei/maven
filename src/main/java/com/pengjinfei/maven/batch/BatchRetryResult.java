package com.pengjinfei.maven.batch;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created on 9/16/17
 *
 * @author Pengjinfei
 */
@Data
public class BatchRetryResult<T extends Serializable> {

    private List<T> successList;

    private List<ErrorCarrier<T>> retryList;

    private List<ErrorCarrier<T>> failList;

}
