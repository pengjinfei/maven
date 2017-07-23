package com.pengjinfei.maven.service;

import com.pengjinfei.maven.dto.CouponSendDto;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

/**
 * Created on 7/23/17
 *
 * @author Pengjinfei
 */
@MessagingGateway(name = "entryGateway", defaultRequestChannel = "normSendChannel")
public interface CouponSendGateWay {

    @Gateway(requestChannel = "normSendChannel")
    void sendCoupon(CouponSendDto sendDto);

    @Gateway(requestChannel = "retrySendChannel")
    void retrySendCoupon(CouponSendDto sendDto);
}
