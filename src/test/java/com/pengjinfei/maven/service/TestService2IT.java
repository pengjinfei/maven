package com.pengjinfei.maven.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Pengjinfei on 6/4/17.
 * Description:
 */
public class TestService2IT {

    TestService testService = new TestService();
    @Test
    public void add() throws Exception {
        int add = testService.add(1, 2);
        Assert.assertEquals(3,add);
    }

    @Test
    public void testArray() {
        Integer[] origin = new Integer[16];
        for (int i = 0; i < 16; i++) {
            origin[i] = i;
        }
        List<Integer> integers = Arrays.asList(origin);
        Collections.shuffle(integers);
        Integer[] ordered = integers.toArray(origin);
        System.out.println(Arrays.toString(ordered));
        String randomAlphabetic = RandomStringUtils.randomAlphabetic(16);
        System.out.println(randomAlphabetic);
        char[] toencode = randomAlphabetic.toCharArray();
        char[] encode = new char[16];
        for (int i = 0; i < 16; i++) {
            encode[i] = toencode[ordered[i]];
        }
        char[] revert = new char[16];
        for (int i = 0; i < 16; i++) {
            revert[i] = encode[integers.indexOf(i)];
        }
        System.out.println(new String(encode));
        System.out.println(new String(revert));
    }

}