package com.pengjinfei.maven.configuration;

import org.slf4j.MDC;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.UUID;

/**
 * Created on 8/19/17
 *
 * @author Pengjinfei
 */
public class ThreadPoolTaskExecutorFacotryBean extends ThreadPoolTaskExecutor implements FactoryBean<ThreadPoolTaskExecutor> {

    private  TaskDecorator decorator=new TaskDecorator() {
        @Override
        public Runnable decorate(final Runnable runnable) {
            return new Runnable() {
                @Override
                public void run() {
                    MDC.put("mdc", UUID.randomUUID().toString());
                    runnable.run();
                }
            };
        }
    };

    @Override
    public ThreadPoolTaskExecutor getObject() {
        setTaskDecorator(decorator);
        initialize();
        return this;
    }

    @Override
    public Class<?> getObjectType() {
        return ThreadPoolTaskExecutor.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
