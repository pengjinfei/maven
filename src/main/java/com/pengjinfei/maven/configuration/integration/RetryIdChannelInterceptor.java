package com.pengjinfei.maven.configuration.integration;

import lombok.Getter;
import lombok.Setter;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.StringUtils;

import java.util.UUID;

/**
 * Created on 8/20/17
 *
 * @author Pengjinfei
 */
@Setter
@Getter
public class RetryIdChannelInterceptor extends ChannelInterceptorAdapter {

    private String retryId = "retryId";

    private String sql;

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        MessageHeaders headers = message.getHeaders();
        Object o = headers.get(retryId);
        if (o == null) {
            message = MessageBuilder.withPayload(message.getPayload())
                    .setHeader(retryId, UUID.randomUUID().toString())
                    .copyHeadersIfAbsent(message.getHeaders())
                    .build();
            if (StringUtils.hasText(sql)) {
                sqlSessionTemplate.insert(sql, message.getPayload());
            }
        }
        return super.preSend(message, channel);
    }
}
