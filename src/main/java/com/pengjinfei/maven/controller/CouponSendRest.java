package com.pengjinfei.maven.controller;

import com.pengjinfei.maven.dto.CouponSendDto;
import com.pengjinfei.maven.service.CouponSendGateWay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created on 7/23/17
 *
 * @author Pengjinfei
 */
@RestController
public class CouponSendRest {

    @Autowired
    private CouponSendGateWay gateWay;

    @PostMapping("/send")
    public Object sendCoupon(@RequestBody CouponSendDto dto) {
        gateWay.retrySendCoupon(dto);
        return "success";
    }
}
