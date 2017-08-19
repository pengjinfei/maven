package com.pengjinfei.maven.service.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.List;
import java.util.Set;

/**
 * Created on 8/19/17
 *
 * @author Pengjinfei
 */
@Service
@Slf4j
public class QuartzService {

    @Autowired
    private Scheduler scheduler;

    public void getJobs() {
        try {
            List<String> triggerGroupNames = scheduler.getTriggerGroupNames();
            for (String triggerGroupName : triggerGroupNames) {
                Set<TriggerKey> triggerKeys = scheduler.getTriggerKeys(GroupMatcher.groupEquals(triggerGroupName));
                for (TriggerKey triggerKey : triggerKeys) {
                    Trigger trigger = scheduler.getTrigger(triggerKey);
                    if (trigger instanceof CronTriggerImpl) {
                        CronTriggerImpl cronTrigger = (CronTriggerImpl) trigger;
                        JobKey jobKey = cronTrigger.getJobKey();
                        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
                    }
                }
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public void reschedule(String triggerName, String newCron) {
        try {
            Trigger trigger = scheduler.getTrigger(TriggerKey.triggerKey(triggerName));
            if (trigger instanceof CronTriggerImpl) {
                CronTriggerImpl cronTrigger = (CronTriggerImpl) trigger;
                cronTrigger.setCronExpression(newCron);
                scheduler.rescheduleJob(TriggerKey.triggerKey(triggerName), cronTrigger);
            }
        } catch (SchedulerException | ParseException e) {
            e.printStackTrace();
        }
    }
}
