package com.pengjinfei.maven.configuration.integration;

import com.pengjinfei.maven.utils.MDCUtils;
import lombok.Data;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.retry.RetryContext;
import org.springframework.retry.context.RetryContextSupport;
import org.springframework.retry.policy.RetryCacheCapacityExceededException;
import org.springframework.retry.policy.RetryContextCache;

import java.lang.reflect.Field;

/**
 * Created on 8/20/17
 *
 * @author Pengjinfei
 */
@Data
@SuppressWarnings("unchecked")
public class RedisRetryContextCache implements RetryContextCache {

    private RedisTemplate template;

    private String key = "retryContext";

    private Field lastExceptionFiled = null;

    {
        try {
            lastExceptionFiled = RetryContextSupport.class.getDeclaredField("lastException");
            lastExceptionFiled.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Override
    public RetryContext get(Object o) {
        RetryContext retryContext = (RetryContext) template.opsForHash().get(key, o);
        String attribute = (String) retryContext.getAttribute("exception.uuid");
        try {
            lastExceptionFiled.set(retryContext,new SimpleThreadException(attribute,retryContext.getAttribute("exception.message").toString()));
        } catch (IllegalAccessException ignored) {
        }
        return retryContext;
    }

    @Override
    public void put(Object o, RetryContext retryContext) throws RetryCacheCapacityExceededException {
        /*
          retryContext的异常信息非常多,影响序列化效率
          可以考虑将其置为 null,或者替换为其他简略信息(时间戳,线程 UUID 等)
         */
        if (retryContext instanceof RetryContextSupport) {
            RetryContextSupport context = (RetryContextSupport) retryContext;
            Message message = (Message) context.getAttribute("message");
            context.setAttribute("exception.uuid", MDCUtils.getId());
            context.setAttribute("message", null);
            Throwable lastThrowable = context.getLastThrowable();
            context.setAttribute("exception.message",lastThrowable.getMessage());
            context.registerThrowable(null);
            template.opsForHash().put(key, o, retryContext);
            context.setAttribute("message", message);
            try {
                lastExceptionFiled.set(context,lastThrowable);
            } catch (IllegalAccessException ignored) {
            }
        } else {
            template.opsForHash().put(key, o, retryContext);
        }
    }

    @Override
    public void remove(Object o) {
        template.opsForHash().delete(key, o);
    }

    @Override
    public boolean containsKey(Object o) {
        return template.opsForHash().hasKey(key, o);
    }
}
