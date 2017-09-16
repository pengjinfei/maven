package com.pengjinfei.maven.batch;

import org.quartz.Trigger;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.PollableChannel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 9/16/17
 *
 * @author Pengjinfei
 */
@Component
public class BatchRetryJobFactoryBean implements FactoryBean<Trigger> {

    @Autowired
    private RedisTemplate template;

    private BatchRetryProcessor processor;

    private PollableChannel requestChannel;

    private MessageChannel delayChannel;

    private RetryIdentifier identifier;

    private void runTask() {
        List<Message> messages = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            messages.add(requestChannel.receive());
        }
        List data = new ArrayList();
        for (Message message : messages) {
            data.add(message.getPayload());
        }
        BatchRetryResult result = processor.process(data);

    }

    @Override
    public Trigger getObject() throws Exception {
        return null;
    }

    @Override
    public Class<?> getObjectType() {
        return Trigger.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
