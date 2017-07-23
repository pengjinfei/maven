package com.pengjinfei.maven.service.bac;

import com.pengjinfei.maven.entity.ThirdPartyMerchantCode;
import com.pengjinfei.maven.entity.ThirdPartyProduct;
import com.pengjinfei.maven.entity.ThirdpartyCodeMerchant;
import com.pengjinfei.maven.repository.ThirdpartyCodeMerchantRepository;
import com.pengjinfei.maven.repository.ThirdpartyMerchantCodeRepository;
import com.pengjinfei.maven.repository.ThirdpartyProductRepository;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created on 6/25/17
 *
 * @author Pengjinfei
 */
@Service
public class ThirdpartyCodeService implements ApplicationContextAware{

    @Autowired
    ThirdpartyMerchantCodeRepository thirdpartyMerchantCodeRepository;
    @Autowired
    ThirdpartyCodeMerchantRepository thirdpartyCodeMerchantRepository;
    @Autowired
    ThirdpartyProductRepository thirdpartyProductRepository;
    @Autowired
    ProductCodeService productCodeService;

    public List<ThirdPartyMerchantCode> getThirdPartyCodesByMerchantId(Long id) {
        ThirdpartyCodeMerchant one = thirdpartyCodeMerchantRepository.findOne(id);
        if (one.getAutoGetList()) {
            CodeListService codeListService = applicationContext.getBean(one.getBeanName(), CodeListService.class);
            return codeListService.getMerchantCodeList();
        }
        return thirdpartyMerchantCodeRepository.findByThirdpartyCodeMerchantId(id);
    }

    public String getThidpartyCode(Long id) {
        ThirdPartyProduct partyProduct = thirdpartyProductRepository.findOne(id);
        return productCodeService.getCode(partyProduct);
    }

    private ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }
}
