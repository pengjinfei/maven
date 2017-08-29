package com.pengjinfei.maven.configuration.integration;

import com.pengjinfei.maven.configuration.cache.FstSerializer;
import com.pengjinfei.maven.entity.Person;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * Created on 7/23/17
 *
 * @author Pengjinfei
 */
@Configuration
@ImportResource(locations = {"classpath:integration.xml"})
public class IntegrationConfiguration {

    @Bean
    public RedisZsetMessageStore zsetMessageStore(RedisConnectionFactory redisConnectionFactory, FstSerializer fstSerializer) {
        RedisZsetMessageStore messageStore = new RedisZsetMessageStore(redisConnectionFactory);
        messageStore.setCalculator(new AbstractMessagePayloadScoreCaculator<Person>() {
            @Override
            public double scorePayload(Person person) {
                return person.getAge().doubleValue();
            }
        });
        messageStore.setValueSerializer(fstSerializer);
        return messageStore;
    }

}
