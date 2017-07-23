package com.pengjinfei.maven.service;

import com.pengjinfei.maven.dto.CouponSendDto;

/**
 * Created on 7/23/17
 *
 * @author Pengjinfei
 */
public interface SendCouponService {

    void sendCoupon(CouponSendDto couponSendDto);

    void retrySendCoupon(CouponSendDto couponSendDto);
}
