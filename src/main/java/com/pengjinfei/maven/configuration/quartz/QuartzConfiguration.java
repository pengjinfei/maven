package com.pengjinfei.maven.configuration.quartz;

import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.List;

import static com.pengjinfei.maven.configuration.Constants.APPLICATION_CONTEXT_KEY;

/**
 * Created on 8/19/17
 *
 * @author Pengjinfei
 */
@Configuration
public class QuartzConfiguration {

    @Bean
    public SchedulerFactoryBean scheduler(@Qualifier("dataSource") DataSource dataSource, PlatformTransactionManager transactionManager) {
        SchedulerFactoryBean factoryBean = new SchedulerFactoryBean(){

            @Autowired
            List<Trigger> triggers;

            @PostConstruct
            public void init() {
                this.setTriggers(triggers.toArray(new Trigger[triggers.size()]));
            }
        };
        factoryBean.setDataSource(dataSource);
        factoryBean.setConfigLocation(new ClassPathResource("quartz.properties"));
        factoryBean.setTransactionManager(transactionManager);
        factoryBean.setApplicationContextSchedulerContextKey(APPLICATION_CONTEXT_KEY);
        return factoryBean;
    }

    @Bean
    public JobDetail testQuartzJobDetail() throws Exception {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setConcurrent(false);
        factoryBean.setName("testQuartzJob");
        factoryBean.setBeanName("quartzDemo");
        factoryBean.setMethodName("testQuartz");
        return factoryBean.getObject();
    }

    @Bean
    public CronTriggerFactoryBean testQuartzTrigger() throws Exception{
        CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();
        factoryBean.setCronExpression("0/15 * * * * ?");
        factoryBean.setJobDetail(testQuartzJobDetail());
        factoryBean.setName("testQuartzTrigger");
        return factoryBean;
    }
}
