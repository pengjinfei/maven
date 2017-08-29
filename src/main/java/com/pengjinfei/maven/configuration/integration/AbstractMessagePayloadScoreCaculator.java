package com.pengjinfei.maven.configuration.integration;

import org.springframework.messaging.Message;

/**
 * Created on 8/29/17
 *
 * @author Pengjinfei
 */
public abstract class AbstractMessagePayloadScoreCaculator<T> implements RedisScoreCaculator<Message<T>> {
    @Override
    public double score(Message<T> tMessage) {
        return scorePayload(tMessage.getPayload());
    }

    abstract public double scorePayload(T t);
}
