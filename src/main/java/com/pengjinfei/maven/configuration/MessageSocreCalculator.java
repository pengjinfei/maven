package com.pengjinfei.maven.configuration;

import org.springframework.messaging.Message;

/**
 * Created on 8/19/17
 *
 * @author Pengjinfei
 */
public interface MessageSocreCalculator {

    double calScore(Message message);
}
