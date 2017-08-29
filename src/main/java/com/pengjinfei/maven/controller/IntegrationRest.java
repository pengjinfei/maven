package com.pengjinfei.maven.controller;

import com.pengjinfei.maven.configuration.integration.RedisZsetMessageStore;
import com.pengjinfei.maven.entity.Person;
import com.pengjinfei.maven.service.integration.GateWayDemo;
import com.pengjinfei.maven.service.integration.IntegrationDemoInterface;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * Created on 8/20/17
 *
 * @author Pengjinfei
 */
@RestController
@RequestMapping("/int")
public class IntegrationRest {

    @Autowired
    private GateWayDemo gateWayDemo;

    @Autowired
    @Qualifier("integrationDemoInterfaceGateway")
    private IntegrationDemoInterface integrationDemoInterface;

    @PostMapping
    public String putIntoGateway(@RequestBody Person person) {
        gateWayDemo.sayHello(person);
        return "success";
    }

    @PostMapping("/ann")
    public String putIntoAnnoGateway(@RequestBody Person person) {
        integrationDemoInterface.sayHello(person);
        return "success";
    }

    @Autowired
    private RedisZsetMessageStore zsetMessageStore;

    @Autowired
    private RedisTemplate template;

    @GetMapping("/zset/{key}")
    public Object getFromZse(@PathVariable("key") String key) {
        return zsetMessageStore.pollMessageFromGroup(key);
    }

    @PostMapping("/zset/{key}")
    public void putInZset(@PathVariable("key") String key, @RequestParam("val") String val) {
        template.opsForZSet().add(key, val, RandomUtils.nextDouble(1.0, 100.0));
    }
}
