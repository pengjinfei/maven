package com.pengjinfei.maven.service.bac;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

/**
 * Created on 6/25/17
 *
 * @author Pengjinfei
 */
@Service
public class MultiSleepBACCodeProvider extends AbstractMultiBACCodeProvider {
    @Override
    public List<String> doGetMulti(Integer thirdpartyCode) {
        List<String> list = new LinkedList<String>();
        for (int i = 0; i < getBatchSize(); i++) {
            String s = RandomStringUtils.randomAlphabetic(6);
            list.add(s);
        }
        return list;
    }
}
