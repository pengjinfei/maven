package com.pengjinfei.maven.configuration.quartz;

import org.quartz.CronTrigger;
import org.quartz.Scheduler;

import java.lang.annotation.*;

/**
 * Created on 8/20/17
 *
 * @author Pengjinfei
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface QuartzJob {

    String name() default "";

    String group() default Scheduler.DEFAULT_GROUP;

    boolean concurrent() default true;

    int misfireInstruction() default CronTrigger.MISFIRE_INSTRUCTION_DO_NOTHING;

    String cronExpression();
}
