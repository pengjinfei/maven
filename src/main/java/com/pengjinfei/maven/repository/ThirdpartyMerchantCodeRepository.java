package com.pengjinfei.maven.repository;

import com.pengjinfei.maven.entity.ThirdPartyMerchantCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Created on 6/25/17
 *
 * @author Pengjinfei
 */
public interface ThirdpartyMerchantCodeRepository extends JpaRepository<ThirdPartyMerchantCode,Long> {

    public List<ThirdPartyMerchantCode> findByThirdpartyCodeMerchantId(Long id);
}
