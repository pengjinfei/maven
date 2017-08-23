package com.pengjinfei.maven.service.integration;

import com.pengjinfei.maven.entity.Person;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Created on 8/20/17
 *
 * @author Pengjinfei
 */
@Service
@Slf4j
public class ServiceActivatorDemo {

    public void sayHello(Person person) {
        log.info("Hello! {}, welcome you!", person);
        BigDecimal age = person.getAge();
        if (age != null) {
            if (age.intValue() % 2 != 0) {
                throw new RuntimeException("test");
            }
        }
    }
}
