package com.pengjinfei.maven.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;

/**
 * Created on 6/14/17
 *
 * @author Pengjinfei
 */
@EnableAsync
@Configuration
@Slf4j
public class AsyncConfiguration implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutorFacotryBean facotryBean = new ThreadPoolTaskExecutorFacotryBean();
        facotryBean.setCorePoolSize(10);
        facotryBean.setMaxPoolSize(20);
        facotryBean.setQueueCapacity(25);
        return facotryBean.getObject();
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return null;
    }
}
