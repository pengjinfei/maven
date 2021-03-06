package com.pengjinfei.maven.configuration.quartz;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

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

         /*   @Autowired
            List<Trigger> triggers;

            @PostConstruct
            public void init() {
                this.setTriggers(triggers.toArray(new Trigger[triggers.size()]));
            }*/
        };
        factoryBean.setDataSource(dataSource);
        factoryBean.setConfigLocation(new ClassPathResource("quartz.properties"));
        factoryBean.setTransactionManager(transactionManager);
        factoryBean.setOverwriteExistingJobs(true);
        factoryBean.setApplicationContextSchedulerContextKey(APPLICATION_CONTEXT_KEY);
        //factoryBean.setAutoStartup(false);
        return factoryBean;
    }
}
