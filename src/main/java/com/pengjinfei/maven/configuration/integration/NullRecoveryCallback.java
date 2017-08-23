package com.pengjinfei.maven.configuration.integration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryContext;

/**
 * Created on 8/23/17
 *
 * @author Pengjinfei
 */
@Slf4j
public class NullRecoveryCallback implements RecoveryCallback {
    @Override
    public Object recover(RetryContext retryContext) throws Exception {
        GenericMessage message = (GenericMessage) retryContext.getAttribute("message");
        log.warn("retry message {} out!",message);
        return null;
    }
}
