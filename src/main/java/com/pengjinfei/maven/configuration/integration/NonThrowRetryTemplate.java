package com.pengjinfei.maven.configuration.integration;

import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.RetryState;
import org.springframework.retry.support.RetryTemplate;

/**
 * Created on 8/27/17
 *
 * @author Pengjinfei
 */
public class NonThrowRetryTemplate extends RetryTemplate {

    @Override
    protected boolean shouldRethrow(RetryPolicy retryPolicy, RetryContext context, RetryState state) {
        return false;
    }

}
