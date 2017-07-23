package com.pengjinfei.maven.enu;

import lombok.Getter;

/**
 * Created on 7/23/17
 *
 * @author Pengjinfei
 */
@Getter
public enum Sender {
    OFFER("offer"),
    EXCEL("excel");

    private String name;
    Sender(String name) {
        this.name=name;
    }
}
