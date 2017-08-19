package com.pengjinfei.maven.configuration.quartz;

import lombok.Data;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.impl.JobDetailImpl;
import org.springframework.beans.factory.FactoryBean;

/**
 * Created on 8/19/17
 *
 * @author Pengjinfei
 */
@Data
public class JobDetailFactoryBean implements FactoryBean<JobDetail> {

    private JobDetail jobDetail;
    private String beanName;
    private String methodName;
    private String jobName;
    private String name;
    private boolean concurrent = true;
    private String group= Scheduler.DEFAULT_GROUP ;

    @Override
    public JobDetail getObject() throws Exception {
        String name = (this.name != null ? this.name : this.beanName);
        JobDetailImpl jdi = new JobDetailImpl();
        jdi.setName(name);
        jdi.setGroup(group);
        if (concurrent) {
            jdi.setJobClass(ApplicationContextQuartzJobBean.class);
        } else {
            jdi.setJobClass(NonConcurrentApplicationContextQuartzJobBean.class);
        }
        jdi.getJobDataMap().put("beanName", beanName);
        jdi.getJobDataMap().put("methodName", methodName);
        //jdi.getJobDataMap().put("jobName", jobName);
        this.jobDetail = jdi;
        return jobDetail;
    }

    @Override
    public Class<?> getObjectType() {
        return JobDetail.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
