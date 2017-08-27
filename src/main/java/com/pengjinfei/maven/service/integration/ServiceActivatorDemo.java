package com.pengjinfei.maven.service.integration;

import com.pengjinfei.maven.entity.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Created on 8/20/17
 *
 * @author Pengjinfei
 */
@Service
@Slf4j
public class ServiceActivatorDemo {

    public void sayHello(Person person) throws Exception {
        log.info("Hello! {}, welcome you!", person);
        int age = person.getAge().intValue();
        if (age % 2 != 0) {
            throw new Exception("test");
        }
    }

    public void sayHelloByQuartz(Person person) {
        log.info("Hello! {}, you are running on quartz!", person);
        int age = person.getAge().intValue();
        if (age % 2 != 0) {
            throw new RuntimeException("quartz");
        }
    }
}
