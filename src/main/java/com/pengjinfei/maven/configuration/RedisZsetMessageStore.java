package com.pengjinfei.maven.configuration;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
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
import org.springframework.messaging.MessageHeaders;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.Calendar;
import java.util.Set;

import static com.pengjinfei.maven.dto.Constants.RETRY_TIMES;

/**
 * Created on 7/23/17
 *
 * @author Pengjinfei
 * @see org.springframework.integration.redis.store.RedisChannelMessageStore
 */
public class RedisZsetMessageStore implements ChannelMessageStore, BeanNameAware, InitializingBean {

    private final RedisTemplate<Object, Message<?>> redisTemplate;

    private volatile MessageGroupFactory messageGroupFactory = new SimpleMessageGroupFactory();

    private String beanName;

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
        MessageHeaders headers = message.getHeaders();
        Integer times = ((Integer) headers.get(RETRY_TIMES));
        long score;
        long timeInMillis = Calendar.getInstance().getTimeInMillis();
        if (times == null) {
            score = timeInMillis*(-1);
        } else {
            score = timeInMillis;
        }
        this.redisTemplate.boundZSetOps(groupId).add(message, score);
        return null;
    }

    @Override
    public Message<?> pollMessageFromGroup(Object groupId) {
        Set<Message<?>> messages = this.redisTemplate.opsForZSet().range(groupId, 0, 0);
        if (CollectionUtils.isEmpty(messages)) {
            return null;
        }
        this.redisTemplate.opsForZSet().removeRange(groupId, 0, 0);
        return messages.iterator().next();
    }

    @Override
    public void removeMessageGroup(Object groupId) {
        this.redisTemplate.boundZSetOps(groupId).removeRange(0,-1);
    }
}
