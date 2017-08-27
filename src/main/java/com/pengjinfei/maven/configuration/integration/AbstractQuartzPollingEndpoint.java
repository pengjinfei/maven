package com.pengjinfei.maven.configuration.integration;

import com.pengjinfei.maven.configuration.quartz.JobDetailFactoryBean;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.aop.Advice;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.integration.endpoint.AbstractEndpoint;
import org.springframework.integration.transaction.IntegrationResourceHolder;
import org.springframework.integration.transaction.IntegrationResourceHolderSynchronization;
import org.springframework.integration.transaction.TransactionSynchronizationFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandlingException;
import org.springframework.messaging.support.ErrorMessage;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created on 8/27/17
 *
 * @author Pengjinfei
 * @see org.springframework.integration.endpoint.AbstractPollingEndpoint
 */
@Setter
@Slf4j
public abstract class AbstractQuartzPollingEndpoint extends AbstractEndpoint implements BeanClassLoaderAware, FactoryBean<Trigger> {

    private volatile List<Advice> adviceChain;
    private volatile long maxMessagesPerPoll = -1;
    private volatile TransactionSynchronizationFactory transactionSynchronizationFactory;
    private volatile ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();
    private volatile boolean initialized;
    private String group = Scheduler.DEFAULT_GROUP;
    private String cronExpress = "0/10 * * * * ?";
    private Callable<Boolean> pollingTask;

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    @Override
    protected void doStart() {
        createPollingTask();
        initialized = true;
    }

    @Override
    protected void doStop() {
        this.initialized = false;
    }

    public void runTask() {
        int count = 0;
        while (initialized && (maxMessagesPerPoll <= 0 || count < maxMessagesPerPoll)) {
            try {
                if (!this.pollingTask.call()) {
                    break;
                }
                count++;
            } catch (Exception e) {
                if (e instanceof RuntimeException) {
                    throw (RuntimeException) e;
                } else {
                    throw new MessageHandlingException(new ErrorMessage(e), e);
                }
            }
        }
    }


    private boolean doPoll() {
        IntegrationResourceHolder holder = this.bindResourceHolderIfNecessary(
                this.getResourceKey(), this.getResourceToBind());
        Message<?> message = null;
        try {
            message = this.receiveMessage();
        } catch (Exception e) {
            if (Thread.interrupted()) {
                log.debug("Poll interrupted - during stop()? : " + e.getMessage());
                return false;
            } else {
                throw (RuntimeException) e;
            }
        }
        boolean result;
        if (message == null) {
            log.debug("Received no Message during the poll, returning 'false'");
            result = false;
        } else {
            log.debug("Poll resulted in Message: " + message);
            if (holder != null) {
                holder.setMessage(message);
            }
            this.handleMessage(message);
            result = true;
        }
        return result;
    }

    protected String getResourceKey() {
        return null;
    }

    protected Object getResourceToBind() {
        return null;
    }


    private IntegrationResourceHolder bindResourceHolderIfNecessary(String key, Object resource) {

        if (this.transactionSynchronizationFactory != null && resource != null) {
            if (TransactionSynchronizationManager.isActualTransactionActive()) {
                TransactionSynchronization synchronization = this.transactionSynchronizationFactory.create(resource);
                TransactionSynchronizationManager.registerSynchronization(synchronization);
                if (synchronization instanceof IntegrationResourceHolderSynchronization) {
                    IntegrationResourceHolder holder =
                            ((IntegrationResourceHolderSynchronization) synchronization).getResourceHolder();
                    if (key != null) {
                        holder.addAttribute(key, resource);
                    }
                    return holder;
                }
            }
        }
        return null;
    }

    protected boolean isReceiveOnlyAdvice(Advice advice) {
        return false;
    }

    private void createPollingTask() {
        List<Advice> receiveOnlyAdviceChain = new ArrayList<Advice>();
        if (!CollectionUtils.isEmpty(this.adviceChain)) {
            for (Advice advice : this.adviceChain) {
                if (isReceiveOnlyAdvice(advice)) {
                    receiveOnlyAdviceChain.add(advice);
                }
            }
        }

        pollingTask = this::doPoll;

        List<Advice> adviceChain = this.adviceChain;
        if (!CollectionUtils.isEmpty(adviceChain)) {
            ProxyFactory proxyFactory = new ProxyFactory(pollingTask);
            if (!CollectionUtils.isEmpty(adviceChain)) {
                for (Advice advice : adviceChain) {
                    if (!isReceiveOnlyAdvice(advice)) {
                        proxyFactory.addAdvice(advice);
                    }
                }
            }
            pollingTask = (Callable<Boolean>) proxyFactory.getProxy(this.beanClassLoader);
        }
        if (receiveOnlyAdviceChain.size() > 0) {
            applyReceiveOnlyAdviceChain(receiveOnlyAdviceChain);
        }
    }

    protected void applyReceiveOnlyAdviceChain(Collection<Advice> chain) {
    }


    protected abstract Message<?> receiveMessage();

    protected abstract void handleMessage(Message<?> message);

    @Override
    public Trigger getObject() throws Exception {
        CronTriggerFactoryBean triggerFactoryBean = new CronTriggerFactoryBean();
        JobDetailFactoryBean jobDetailFactoryBean = new JobDetailFactoryBean();
        jobDetailFactoryBean.setBeanName("&"+getComponentName());
        jobDetailFactoryBean.setMethodName("runTask");
        jobDetailFactoryBean.setGroup(group);
        jobDetailFactoryBean.setName(getComponentName() + "Job");
        JobDetail jobDetail = jobDetailFactoryBean.getObject();
        triggerFactoryBean.setJobDetail(jobDetail);
        triggerFactoryBean.setName(getComponentName() + "trigger");
        triggerFactoryBean.setCronExpression(cronExpress);
        triggerFactoryBean.setGroup(group);
        triggerFactoryBean.afterPropertiesSet();
        return triggerFactoryBean.getObject();
    }

    @Override
    public Class<?> getObjectType() {
        return Trigger.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
