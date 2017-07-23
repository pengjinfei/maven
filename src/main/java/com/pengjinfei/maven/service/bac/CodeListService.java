package com.pengjinfei.maven.service.bac;

import com.pengjinfei.maven.entity.ThirdPartyMerchantCode;

import java.util.List;

/**
 * Created on 6/25/17
 *
 * @author Pengjinfei
 */
public interface CodeListService {

    List<ThirdPartyMerchantCode> getMerchantCodeList();
}
