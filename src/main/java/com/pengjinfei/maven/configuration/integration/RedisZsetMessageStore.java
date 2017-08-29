package com.pengjinfei.maven.configuration.integration;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.integration.store.ChannelMessageStore;
import org.springframework.integration.store.MessageGroup;
import org.springframework.integration.store.MessageGroupFactory;
import org.springframework.integration.store.SimpleMessageGroupFactory;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.messaging.Message;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

/**
 * Created on 7/23/17
 *
 * @author Pengjinfei
 * @see org.springframework.integration.redis.store.RedisChannelMessageStore
 */
@Setter
@Getter
@Slf4j
public class RedisZsetMessageStore implements ChannelMessageStore, BeanNameAware, InitializingBean {

    private final RedisTemplate<Object, Message<?>> redisTemplate;

    private volatile MessageGroupFactory messageGroupFactory = new SimpleMessageGroupFactory();

    private String beanName;

    private RedisScoreCaculator calculator;

    public RedisZsetMessageStore(RedisConnectionFactory connectionFactory) {
        this.redisTemplate = new RedisTemplate<Object, Message<?>>();
        this.redisTemplate.setConnectionFactory(connectionFactory);
        this.redisTemplate.setKeySerializer(new StringRedisSerializer());
        this.redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
        this.redisTemplate.afterPropertiesSet();
    }

    public void setValueSerializer(RedisSerializer<?> valueSerializer) {
        Assert.notNull(valueSerializer, "'valueSerializer' must not be null");
        this.redisTemplate.setValueSerializer(valueSerializer);
    }

    public void setMessageGroupFactory(MessageGroupFactory messageGroupFactory) {
        Assert.notNull(messageGroupFactory, "'messageGroupFactory' must not be null");
        this.messageGroupFactory = messageGroupFactory;
    }

    protected MessageGroupFactory getMessageGroupFactory() {
        return this.messageGroupFactory;
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }


    protected String getBeanName() {
        return this.beanName;
    }

    protected RedisTemplate<Object, Message<?>> getRedisTemplate() {
        return this.redisTemplate;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    @ManagedAttribute
    public int messageGroupSize(Object groupId) {
        return this.redisTemplate.opsForZSet().size(groupId).intValue();
    }

    @Override
    public MessageGroup getMessageGroup(Object groupId) {
        Set<Message<?>> messages = this.redisTemplate.boundZSetOps(groupId).range(0, -1);
        return getMessageGroupFactory().create(messages, groupId);
    }

    @Override
    public MessageGroup addMessageToGroup(Object groupId, Message<?> message) {
        this.redisTemplate.boundZSetOps(groupId).add(message, calculator.score(message));
        return null;
    }

    @Override
    public Message<?> pollMessageFromGroup(Object groupId) {
        RedisSerializer<Object> keySerializer = (RedisSerializer<Object>) redisTemplate.getKeySerializer();
        RedisSerializer<Message> valueSerializer = (RedisSerializer<Message>) redisTemplate.getValueSerializer();
        try {
            Object execute = redisTemplate.execute(new RedisCallback<Object>() {
                @Override
                public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                    redisConnection.multi();
                    byte[] bytes = keySerializer.serialize(groupId);
                    redisConnection.zRange(bytes, 0, 0);
                    redisConnection.zRemRange(bytes, 0, 0);
                    return redisConnection.exec();
                }

            });
            if (execute instanceof List) {
                List list = (List) execute;
                Object o = list.get(0);
                if (o != null) {
                    Set set = (Set) o;
                    if (CollectionUtils.isEmpty(set)) {
                        return null;
                    }
                    byte[] bytes = (byte[]) set.iterator().next();
                    return valueSerializer.deserialize(bytes);
                } else {
                    return null;
                }
            }
        } catch (Exception e) {
            log.error("Get message from {} faild.", groupId, e);
        }
        return null;
    }

    @Override
    public void removeMessageGroup(Object groupId) {
        this.redisTemplate.boundZSetOps(groupId).removeRange(0,-1);
    }
}
