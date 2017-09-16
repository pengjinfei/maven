package com.pengjinfei.maven.batch;

import lombok.Data;

import java.io.Serializable;

/**
 * Created on 9/16/17
 *
 * @author Pengjinfei
 */
@Data
public class ErrorCarrier<T extends Serializable> {

    private T origin;

    private String errorMessage;
}
