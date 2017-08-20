package com.pengjinfei.maven.configuration.integration;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.MessageBuilder;

import java.util.UUID;

/**
 * Created on 8/20/17
 *
 * @author Pengjinfei
 */
public class RetryIdChannelInterceptor extends ChannelInterceptorAdapter {

    private String retryId = "retryId";

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        MessageHeaders headers = message.getHeaders();
        Object o = headers.get(retryId);
        if (o == null) {
            message = MessageBuilder.withPayload(message.getPayload())
                    .setHeader(retryId, UUID.randomUUID().toString())
                    .copyHeadersIfAbsent(message.getHeaders())
                    .build();
        }
        return super.preSend(message, channel);
    }
}
