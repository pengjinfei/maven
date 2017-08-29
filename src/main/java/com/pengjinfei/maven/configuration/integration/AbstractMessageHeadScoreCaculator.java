package com.pengjinfei.maven.configuration.integration;

import lombok.Data;
import org.springframework.messaging.Message;

/**
 * Created on 8/29/17
 *
 * @author Pengjinfei
 */
@Data
public abstract class AbstractMessageHeadScoreCaculator<T> implements RedisScoreCaculator<Message> {

    private String scoreKey;

    public AbstractMessageHeadScoreCaculator(String scoreKey) {
        this.scoreKey = scoreKey;
    }

    @Override
    public double score(Message message) {
        T o = (T) message.getHeaders().get(scoreKey);
        return scoreHead(o);
    }

    abstract double scoreHead(T t);


}
