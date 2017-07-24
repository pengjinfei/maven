package com.pengjinfei.maven.service.impl;

import com.pengjinfei.maven.dto.CouponSendDto;
import com.pengjinfei.maven.service.CouponSendGateWay;
import com.pengjinfei.maven.service.SendCouponService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Created on 7/23/17
 *
 * @author Pengjinfei
 */
@Service
@Slf4j
public class SendCouponServiceImpl implements SendCouponService {

    @Autowired
    private CouponSendGateWay gateWay;

    @Override
    @ServiceActivator(inputChannel = "normSendChannel",
            poller = {
                    @Poller(fixedRate = "1000", maxMessagesPerPoll = "10")
            })
    @Async
    public void sendCoupon(CouponSendDto couponSendDto) {
        int i = RandomUtils.nextInt(0, 10);
        if (i / 2 == 0) {
            gateWay.retrySendCoupon(couponSendDto);
        } else {
            log.info("send coupon success. {}", couponSendDto);
        }
    }

    @Override
    @ServiceActivator(inputChannel = "retrySendChannel",
            poller = {
                    @Poller("retryPoller")
            })
    //@Async
    public void retrySendCoupon(CouponSendDto couponSendDto) {
        System.out.println(couponSendDto);
        throw new RuntimeException("test failed");
        //log.info("retry send coupon success. {}", couponSendDto);
    }
}
