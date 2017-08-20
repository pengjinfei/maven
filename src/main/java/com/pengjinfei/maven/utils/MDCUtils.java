package com.pengjinfei.maven.utils;

import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import java.util.UUID;

/**
 * Created on 8/19/17
 *
 * @author Pengjinfei
 */
public class MDCUtils {

    private static final String ID = "id";
    private static final String USER = "user";

    public static void putIfAbsent(String key, String value) {
        String s = MDC.get(key);
        if (!StringUtils.hasLength(s)) {
            MDC.put(key,value);
        }
    }

    public static String getId() {
        return MDC.get(ID);
    }

    public static void setId() {
        putIfAbsent(ID, UUID.randomUUID().toString());
    }

    public static void setUser(String user) {
        putIfAbsent(USER,user);
    }

    public static void clear() {
        MDC.clear();
    }
}
