package com.pengjinfei.maven.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Created on 6/25/17
 *
 * @author Pengjinfei
 */
@Entity
@Data
public class ThirdPartyMerchantCode {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private ThirdpartyCodeMerchant thirdpartyCodeMerchant;

    private Integer code;

    private String name;

    private String description;

}
