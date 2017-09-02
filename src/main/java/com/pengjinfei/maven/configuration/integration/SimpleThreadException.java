package com.pengjinfei.maven.configuration.integration;

import lombok.Getter;
import lombok.Setter;

/**
 * Created on 9/2/17
 *
 * @author Pengjinfei
 */
@Setter
@Getter
public class SimpleThreadException extends Exception {

    private String uuid;

    public SimpleThreadException(String uuid, String message) {
        super(message);
        this.uuid=uuid;
    }

    @Override
    public String toString() {
        return "uuid: " + uuid + ", message: " + getMessage();
    }
}
