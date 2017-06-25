package com.pengjinfei.maven.repository;

import com.pengjinfei.maven.entity.ThirdPartyProduct;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created on 6/25/17
 *
 * @author Pengjinfei
 */
public interface ThirdpartyProductRepository extends JpaRepository<ThirdPartyProduct,Long>{
}
