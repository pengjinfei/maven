package com.pengjinfei.maven.controller;

import com.pengjinfei.maven.service.bac.ThirdpartyCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created on 6/25/17
 *
 * @author Pengjinfei
 */
@RestController
public class ThirdpartyCodeController {
    
    @Autowired
    ThirdpartyCodeService  thirdpartyCodeService;

    @GetMapping("/thirdpartyMerchants/{id}/codes")
    public Object getThirdpartyMerchantCodes(@PathVariable("id") Long id) {
        return thirdpartyCodeService.getThirdPartyCodesByMerchantId(id); 
    }

    @GetMapping("/thirdpartyProducts/{id}/code")
    public Object getThirdpartyCode(@PathVariable("id") Long id) {
        return thirdpartyCodeService.getThidpartyCode(id);
    }
}
