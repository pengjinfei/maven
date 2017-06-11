package com.pengjinfei.maven;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by Pengjinfei on 6/4/17.
 * Description:
 */
@SpringBootApplication
public final class Application {

    /**
     * private constructor.
     */
    private Application() {

    }

    /**
     * main method.
     * @param args args
     */
    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
