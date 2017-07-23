package com.pengjinfei.maven.dto;

import com.alibaba.fastjson.JSONObject;
import com.pengjinfei.maven.enu.Sender;
import lombok.Data;

import java.io.Serializable;

/**
 * Created on 7/23/17
 *
 * @author Pengjinfei
 */
@Data
public class CouponSendDto implements Serializable{

    private String couponCode;
    private String orgCode;
    private Sender sender;
    private JSONObject context;
}
