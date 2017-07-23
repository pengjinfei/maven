package com.pengjinfei.maven.configuration;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.integration.util.CompoundTrigger;
import org.springframework.scheduling.Trigger;
import org.springframework.util.Assert;

/**
 * Created on 7/23/17
 *
 * @author Pengjinfei
 */
public class MyCompoundTriggerAdvice implements MethodInterceptor {

    private final CompoundTrigger compoundTrigger;

    private final Trigger override;

    public MyCompoundTriggerAdvice(CompoundTrigger compoundTrigger, Trigger overrideTrigger) {
        Assert.notNull(compoundTrigger, "'compoundTrigger' cannot be null");
        this.compoundTrigger = compoundTrigger;
        this.override = overrideTrigger;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object proceed = invocation.proceed();
        if (proceed instanceof Boolean) {
            Boolean aBoolean = (Boolean) proceed;
            if (aBoolean) {
                this.compoundTrigger.setOverride(null);
            }
            else {
                this.compoundTrigger.setOverride(this.override);
            }
        }
        return proceed;
    }
}
