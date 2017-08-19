package com.pengjinfei.maven.configuration;

import com.pengjinfei.maven.utils.MDCUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Created on 8/19/17
 *
 * @author Pengjinfei
 */
public class ThreadPoolTaskExecutorFacotryBean extends ThreadPoolTaskExecutor implements FactoryBean<ThreadPoolTaskExecutor> {

    private  TaskDecorator decorator= runnable -> () -> {
        MDCUtils.setId();
        runnable.run();
        MDCUtils.clear();
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
