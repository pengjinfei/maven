package com.pengjinfei.maven.configuration.quartz;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.PersistJobDataAfterExecution;

/**
 * Created on 8/19/17
 *
 * @author Pengjinfei
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class
NonConcurrentApplicationContextQuartzJobBean extends ApplicationContextQuartzJobBean{
}
