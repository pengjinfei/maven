package com.pengjinfei.maven.configuration;

import lombok.Getter;
import lombok.Setter;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.integration.support.MutableMessageHeaders;
import org.springframework.integration.util.CompoundTrigger;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.Trigger;
import org.springframework.util.Assert;

import java.util.Calendar;

import static com.pengjinfei.maven.dto.Constants.RETRY_HANDLE_DATE;
import static com.pengjinfei.maven.dto.Constants.RETRY_TIMES;

/**
 * Created on 7/23/17
 *
 * @author Pengjinfei
 */
@Setter
@Getter
public class MyCompoundTriggerAdvice extends ChannelInterceptorAdapter implements MethodInterceptor {

    private final CompoundTrigger compoundTrigger;

    private final Trigger override;

    private int maxRetryTimes = 0;

    private int retryInterval = 1;

    private MessageChannel channel;

    private ThreadLocal<Message> messageThreadLocal =new ThreadLocal<Message>();

    public MyCompoundTriggerAdvice(CompoundTrigger compoundTrigger, Trigger overrideTrigger) {
        Assert.notNull(compoundTrigger, "'compoundTrigger' cannot be null");
        this.compoundTrigger = compoundTrigger;
        this.override = overrideTrigger;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Object proceed = null;
        try {
            proceed = invocation.proceed();
        } catch (Throwable throwable) {
            Message message = messageThreadLocal.get();
            if (message != null && maxRetryTimes > 0){
                MessageHeaders headers = message.getHeaders();
                Integer times = ((Integer) headers.get(RETRY_TIMES));
                if (times == null) {
                    times = 1;
                } else {
                    times = times + 1;
                }
                if (times < maxRetryTimes) {
                    MessageHeaders newHeaders = new MutableMessageHeaders(headers);
                    newHeaders.put(RETRY_TIMES, times);
                    newHeaders.put(RETRY_HANDLE_DATE, Calendar.getInstance().getTimeInMillis());
                    Message<Object> newMessage = MessageBuilder.createMessage(message.getPayload(), newHeaders);
                    channel.send(newMessage);
                }
            }
            throw throwable;
        }
        if (proceed instanceof Boolean) {
            Boolean aBoolean = (Boolean) proceed;
            if (aBoolean) {
                this.compoundTrigger.setOverride(null);
            } else {
                this.compoundTrigger.setOverride(this.override);
            }
        }
        return proceed;
    }

    @Override
    public Message<?> postReceive(Message<?> message, MessageChannel channel) {
        messageThreadLocal.set(message);
        this.channel=channel;
        if (message == null) {
            return null;
        }
        MessageHeaders headers = message.getHeaders();
        Long time = (Long) headers.get(RETRY_HANDLE_DATE);
        if (time != null && retryInterval > 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            Calendar now = Calendar.getInstance();
            calendar.add(Calendar.HOUR, retryInterval);
            if (now.before(calendar)) {
                this.compoundTrigger.setOverride(this.override);
            }
        }
        return super.postReceive(message, channel);
    }
}
