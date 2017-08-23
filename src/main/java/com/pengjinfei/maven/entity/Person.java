package com.pengjinfei.maven.entity;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created on 8/20/17
 *
 * @author Pengjinfei
 */
@Data
public class Person implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY,generator = "select HIBERNATE_SEQUENCE.nextval from dual")
    private BigDecimal id;
    private String name;
    private BigDecimal age;
    private String address;
}
