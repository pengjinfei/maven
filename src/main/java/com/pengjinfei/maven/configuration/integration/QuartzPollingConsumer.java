package com.pengjinfei.maven.configuration.integration;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.Lifecycle;
import org.springframework.integration.channel.ExecutorChannelInterceptorAware;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.endpoint.IntegrationConsumer;
import org.springframework.integration.router.MessageRouter;
import org.springframework.integration.transaction.IntegrationResourceHolder;
import org.springframework.messaging.*;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.ExecutorChannelInterceptor;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

/**
 * Created on 8/27/17
 *
 * @author Pengjinfei
 * @see org.springframework.integration.endpoint.PollingConsumer
 */
@Setter
@Getter
@Slf4j
public class QuartzPollingConsumer extends AbstractQuartzPollingEndpoint implements IntegrationConsumer {

    private final PollableChannel inputChannel;

    private final MessageHandler handler;

    private final List<ChannelInterceptor> channelInterceptors;

    private volatile long receiveTimeout = 1000;

    public QuartzPollingConsumer(PollableChannel inputChannel, MessageHandler handler) {
        Assert.notNull(inputChannel, "inputChannel must not be null");
        Assert.notNull(handler, "handler must not be null");
        this.inputChannel = inputChannel;
        this.handler = handler;
        if (this.inputChannel instanceof ExecutorChannelInterceptorAware) {
            this.channelInterceptors = ((ExecutorChannelInterceptorAware) this.inputChannel).getChannelInterceptors();
        } else {
            this.channelInterceptors = null;
        }
    }


    @Override
    protected void doStart() {
        if (this.handler instanceof Lifecycle) {
            ((Lifecycle) this.handler).start();
        }
        super.doStart();
    }

    @Override
    protected void doStop() {
        if (this.handler instanceof Lifecycle) {
            ((Lifecycle) this.handler).stop();
        }
        super.doStop();
    }

    @Override
    protected void handleMessage(Message<?> message) {
        Message<?> theMessage = message;
        Deque<ExecutorChannelInterceptor> interceptorStack = null;
        try {
            if (this.channelInterceptors != null
                    && ((ExecutorChannelInterceptorAware) this.inputChannel).hasExecutorInterceptors()) {
                interceptorStack = new ArrayDeque<ExecutorChannelInterceptor>();
                theMessage = applyBeforeHandle(theMessage, interceptorStack);
                if (theMessage == null) {
                    return;
                }
            }
            this.handler.handleMessage(theMessage);
            if (!CollectionUtils.isEmpty(interceptorStack)) {
                triggerAfterMessageHandled(theMessage, null, interceptorStack);
            }
        } catch (Exception ex) {
            if (!CollectionUtils.isEmpty(interceptorStack)) {
                triggerAfterMessageHandled(theMessage, ex, interceptorStack);
            }
            if (ex instanceof MessagingException) {
                throw (MessagingException) ex;
            }
            String description = "Failed to handle " + theMessage + " to " + this + " in " + this.handler;
            throw new MessageDeliveryException(theMessage, description, ex);
        } catch (Error ex) { //NOSONAR - ok, we re-throw below
            if (!CollectionUtils.isEmpty(interceptorStack)) {
                String description = "Failed to handle " + theMessage + " to " + this + " in " + this.handler;
                triggerAfterMessageHandled(theMessage,
                        new MessageDeliveryException(theMessage, description, ex),
                        interceptorStack);
            }
            throw ex;
        }
    }

    private Message<?> applyBeforeHandle(Message<?> message, Deque<ExecutorChannelInterceptor> interceptorStack) {
        Message<?> theMessage = message;
        for (ChannelInterceptor interceptor : this.channelInterceptors) {
            if (interceptor instanceof ExecutorChannelInterceptor) {
                ExecutorChannelInterceptor executorInterceptor = (ExecutorChannelInterceptor) interceptor;
                theMessage = executorInterceptor.beforeHandle(theMessage, this.inputChannel, this.handler);
                if (message == null) {
                        log.debug(executorInterceptor.getClass().getSimpleName()
                                + " returned null from beforeHandle, i.e. precluding the send.");
                    triggerAfterMessageHandled(null, null, interceptorStack);
                    return null;
                }
                interceptorStack.add(executorInterceptor);
            }
        }
        return theMessage;
    }

    private void triggerAfterMessageHandled(Message<?> message, Exception ex,
                                            Deque<ExecutorChannelInterceptor> interceptorStack) {
        Iterator<ExecutorChannelInterceptor> iterator = interceptorStack.descendingIterator();
        while (iterator.hasNext()) {
            ExecutorChannelInterceptor interceptor = iterator.next();
            try {
                interceptor.afterMessageHandled(message, this.inputChannel, this.handler, ex);
            } catch (Throwable ex2) { //NOSONAR
                log.error("Exception from afterMessageHandled in " + interceptor, ex2);
            }
        }
    }

    @Override
    protected Message<?> receiveMessage() {
        return (this.receiveTimeout >= 0)
                ? this.inputChannel.receive(this.receiveTimeout)
                : this.inputChannel.receive();
    }

    @Override
    protected Object getResourceToBind() {
        return this.inputChannel;
    }

    @Override
    protected String getResourceKey() {
        return IntegrationResourceHolder.INPUT_CHANNEL;
    }

    @Override
    public MessageChannel getOutputChannel() {
        if (this.handler instanceof MessageProducer) {
            return ((MessageProducer) this.handler).getOutputChannel();
        }
        else if (this.handler instanceof MessageRouter) {
            return ((MessageRouter) this.handler).getDefaultOutputChannel();
        }
        else {
            return null;
        }
    }
}
