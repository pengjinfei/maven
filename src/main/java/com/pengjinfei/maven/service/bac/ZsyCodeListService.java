package com.pengjinfei.maven.service.bac;

import com.pengjinfei.maven.entity.ThirdPartyMerchantCode;
import com.pengjinfei.maven.service.bac.CodeListService;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

/**
 * Created on 6/25/17
 *
 * @author Pengjinfei
 */
@Service
public class ZsyCodeListService implements CodeListService {
    @Override
    public List<ThirdPartyMerchantCode> getMerchantCodeList() {
        List<ThirdPartyMerchantCode> res = new LinkedList<ThirdPartyMerchantCode>();
        for (int i = 0; i < 10; i++) {
            int i1 = RandomUtils.nextInt(10000, 1000000);
            ThirdPartyMerchantCode code = new ThirdPartyMerchantCode();
            res.add(code);
            code.setCode(i1);
        }
        return res;
    }
}
