package com.pengjinfei.maven.controller;

import com.pengjinfei.maven.entity.Person;
import com.pengjinfei.maven.service.integration.GateWayDemo;
import com.pengjinfei.maven.service.integration.IntegrationDemoInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @Qualifier("integrationDemoInterfaceImplGateway")
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
}
