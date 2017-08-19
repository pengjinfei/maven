package com.pengjinfei.maven.configuration.quartz;

import com.pengjinfei.maven.utils.MDCUtils;
import org.springframework.scheduling.quartz.SimpleThreadPoolTaskExecutor;

/**
 * Created on 8/19/17
 *
 * @author Pengjinfei
 */
public class DecoratoredThreadPool extends SimpleThreadPoolTaskExecutor {

    @Override
    public boolean runInThread(Runnable runnable) {
        Runnable decorated = () -> {
            MDCUtils.setId();
            runnable.run();
            MDCUtils.clear();
        };
        return super.runInThread(decorated);
    }
}
