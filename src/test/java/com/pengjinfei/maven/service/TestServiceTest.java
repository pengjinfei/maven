package com.pengjinfei.maven.service;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Pengjinfei on 6/4/17.
 * Description:
 */
public class TestServiceTest {

    TestService testService = new TestService();

    @Test
    public void add() throws Exception {
        int add = testService.add(1, 2);
        Assert.assertEquals(3,add);
    }

}