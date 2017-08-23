package com.pengjinfei.maven;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
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
//@EnableJpaRepositories
@EnableTransactionManagement
@EnableIntegration
//@IntegrationComponentScan
@Slf4j
@MapperScan(basePackages = "com.pengjinfei.maven.mapper")
public class Application {

    /**
     * main method.
     * @param args args
     */
    public static void main(final String[] args) {

        SpringApplication.run(Application.class, args);
    }

}
