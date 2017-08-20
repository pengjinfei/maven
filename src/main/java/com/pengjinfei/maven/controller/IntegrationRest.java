package com.pengjinfei.maven.controller;

import com.pengjinfei.maven.dto.Person;
import com.pengjinfei.maven.service.integration.GateWayDemo;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping
    public String putIntoGateway(@RequestBody Person person) {
        gateWayDemo.sayHello(person);
        return "success";
    }
}
