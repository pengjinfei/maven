package com.pengjinfei.maven;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Pengjinfei on 6/4/17.
 * Description:
 */
@SpringBootApplication
@RestController
public class Application {

    /**
     * main method.
     * @param args args
     */
    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @GetMapping("/hello")
    public String sayHello() {
        return "hello";
    }

}
