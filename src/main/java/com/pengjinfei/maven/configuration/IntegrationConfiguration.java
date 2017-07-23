package com.pengjinfei.maven.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.integration.aop.CompoundTriggerAdvice;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.core.Pollers;
import org.springframework.integration.redis.store.RedisChannelMessageStore;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.integration.store.ChannelMessageStore;
import org.springframework.integration.store.MessageGroupQueue;
import org.springframework.integration.util.CompoundTrigger;
import org.springframework.scheduling.support.CronTrigger;

/**
 * Created on 7/23/17
 *
 * @author Pengjinfei
 */
@Configuration
public class IntegrationConfiguration {

    @Bean
    public ChannelMessageStore store(RedisConnectionFactory connectionFactory) {
        RedisChannelMessageStore store = new RedisChannelMessageStore(connectionFactory);
        store.setValueSerializer(new FstSerializer());
        return store;
    }

    @Bean
    public QueueChannel normSendChannel(RedisConnectionFactory connectionFactory) {
        MessageGroupQueue queue = new MessageGroupQueue(store(connectionFactory), "normSendChannel");
        return new QueueChannel(queue);
    }

   @Bean
    public PollerMetadata retryPoller() {
        CronTrigger primary= new CronTrigger("0/3 * * * * *");
        CronTrigger  secondary= new CronTrigger("0 */1 * * * *");
        CompoundTrigger compoundTrigger = new CompoundTrigger(primary);
        return Pollers.trigger(compoundTrigger)
                //.maxMessagesPerPoll(10)
                .advice(new CompoundTriggerAdvice(compoundTrigger, secondary))
                .get();
    }

    @Bean
    QueueChannel retrySendChannel(RedisConnectionFactory connectionFactory) {
        MessageGroupQueue queue = new MessageGroupQueue(store(connectionFactory), "retrySendChannel");
        return new QueueChannel(queue);
    }
}
