package com.pengjinfei.maven.controller;

import com.alibaba.fastjson.JSONObject;
import com.pengjinfei.maven.service.quartz.QuartzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created on 8/19/17
 *
 * @author Pengjinfei
 */
@RestController
@RequestMapping("/quartz")
public class QuartzRest {

    @Autowired
    private QuartzService quartzService;

    @GetMapping("/jobs")
    public void getJobs() {
        quartzService.getJobs();
    }

    @PostMapping("/trigger/{triggerKey}")
    public String updateCron(@PathVariable("triggerKey") String triggerKey, @RequestBody JSONObject jsonObject) {
        String cron = jsonObject.getString("cron");
        quartzService.reschedule(triggerKey,cron);
        return "success";
    }
}
