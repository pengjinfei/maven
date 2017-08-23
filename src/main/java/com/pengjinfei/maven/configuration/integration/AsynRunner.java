package com.pengjinfei.maven.configuration.integration;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * Created on 8/21/17
 *
 * @author Pengjinfei
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface AsynRunner {

    /*
    异步执行的 cron 表达式
     */
    String cron();

    /*
    每次执行的条数,默认执行到无数据为止
     */
    long maxPerPoll() default -1;

    /*
    需要持久到数据库是的 mybatis sqlId
     */
    String insertSqlId() default "";

    /*
    执行完成后翻转状态的 mybatis sqlId
     */
    String updateSqlId() default "";

    /*
    重试失败之后执行的 mybatis sqlId
     */
    String failedSqlId() default "";

    /*
    重试的 cron 表达式,为空不重试
     */
    String retryCron() default "";

    /*
    失败之后的重试次数
     */
    int retryTime() default 3;

    /*
    重试的时间间隔
     */
    int delayedTime() default 1;

    /*
    重试的时间间隔单位
     */
    TimeUnit timeUnit() default TimeUnit.HOURS;
}
