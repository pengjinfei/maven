package com.pengjinfei.maven;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Pengjinfei on 6/4/17.
 * Description:
 */
@SpringBootApplication
@EnableAspectJAutoProxy
@RestController
@EnableJpaRepositories
@EnableTransactionManagement
@EnableIntegration
//@IntegrationComponentScan
@Slf4j
public class Application {

    /**
     * main method.
     * @param args args
     */
    public static void main(final String[] args) {

        SpringApplication.run(Application.class, args);
    }

}
