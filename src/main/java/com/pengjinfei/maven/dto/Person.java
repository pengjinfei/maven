package com.pengjinfei.maven.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Created on 8/20/17
 *
 * @author Pengjinfei
 */
@Data
public class Person implements Serializable{

    private String name;
    private Integer age;
}
