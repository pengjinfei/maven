package com.pengjinfei.maven.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created on 6/25/17
 *
 * @author Pengjinfei
 */
@Entity
@Data
public class ThirdpartyCodeMerchant {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private Boolean autoGetList;
    private String beanName;
}
