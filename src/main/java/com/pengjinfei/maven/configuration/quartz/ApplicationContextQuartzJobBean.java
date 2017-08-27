package com.pengjinfei.maven.configuration.quartz;

import com.pengjinfei.maven.configuration.Constants;
import com.pengjinfei.maven.utils.MDCUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;

/**
 * Created by Pengjinfei on 16/7/28.
 * Description:
 */
@Slf4j
@Setter
@Getter
public class ApplicationContextQuartzJobBean extends QuartzJobBean {

    private String beanName;

    private String methodName;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            Object object = getBean(context, beanName);
            Class<?> userClass = ClassUtils.getUserClass(object);
            Method declaredMethod = userClass.getMethod(methodName);
            String name = ((CronTriggerImpl) context.getTrigger()).getName();
            MDCUtils.setUser(name);
            long begin = System.currentTimeMillis();
            log.debug("begin to execute...");
            context.setResult(declaredMethod.invoke(object));
            long end = System.currentTimeMillis();
            log.debug("Execute complete in " + (end - begin) + " ms.");
        } catch (Exception e) {
            throw new JobExecutionException(e);
        } finally {
            MDCUtils.clear();
        }
    }

    protected Object getBean(JobExecutionContext context, String beanName) throws SchedulerException {
        ApplicationContext applicationContext = (ApplicationContext) context.getScheduler().getContext().get(Constants.APPLICATION_CONTEXT_KEY);
        return applicationContext.getBean(beanName);
    }
}
