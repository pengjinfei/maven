package com.pengjinfei.maven.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
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
    public ChannelMessageStore retryStore(RedisConnectionFactory connectionFactory) {
        RedisZsetMessageStore store = new RedisZsetMessageStore(connectionFactory);
        //store.setValueSerializer(new FstSerializer());
        return store;
    }

    @Bean
    public MyCompoundTriggerAdvice retryAdvice(RedisConnectionFactory connectionFactory) {
        CronTrigger primary = new CronTrigger("0/3 * * * * *");
        CronTrigger secondary= new CronTrigger("0 */1 * * * *");
        CompoundTrigger compoundTrigger = new CompoundTrigger(primary);
        MyCompoundTriggerAdvice advice = new MyCompoundTriggerAdvice(compoundTrigger, secondary);
        advice.setRetryInterval(1);
        advice.setMaxRetryTimes(5);
        return advice;
    }

    @Bean
    public QueueChannel normSendChannel(RedisConnectionFactory connectionFactory) {
        MessageGroupQueue queue = new MessageGroupQueue(store(connectionFactory), "normSendChannel");
        return new QueueChannel(queue);
    }

    @Bean
    public PollerMetadata retryPoller(RedisConnectionFactory connectionFactory) {
        MyCompoundTriggerAdvice advice = retryAdvice(connectionFactory);
        return Pollers.trigger(advice.getCompoundTrigger())
                .maxMessagesPerPoll(10)
                .advice(advice)
                .get();
    }

    @Bean
    public QueueChannel retrySendChannel(RedisConnectionFactory connectionFactory) {
        MessageGroupQueue queue = new MessageGroupQueue(retryStore(connectionFactory), "retrySendChannel");
        QueueChannel channel = new QueueChannel(queue);
        channel.addInterceptor(retryAdvice(connectionFactory));
        return channel;
    }

   /* @Bean
    public RequestHandlerRetryAdvice retryAdvice(RedisConnectionFactory connectionFactory) {
        RequestHandlerRetryAdvice advice = new RequestHandlerRetryAdvice();
        advice.setRecoveryCallback(new ErrorMessageSendingRecoverer(retrySendChannel(connectionFactory)));

        RetryTemplate template=new RetryTemplate();
        template.setRetryPolicy(new SimpleRetryPolicy(3));

        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(1000);
        backOffPolicy.setMultiplier(2.0);
        backOffPolicy.setMaxInterval(40000);

        template.setBackOffPolicy(backOffPolicy);

        advice.setRetryTemplate(template);
        return advice;
    }*/

}

