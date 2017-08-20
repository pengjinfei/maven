package com.pengjinfei.maven.configuration.integration;


import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.integration.support.MutableMessage;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.objenesis.Objenesis;
import org.springframework.objenesis.ObjenesisStd;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created on 7/24/17
 *
 * @author Pengjinfei
 */
public class ProtoRedisSerializer implements RedisSerializer<Message> {

    public static ProtoRedisSerializer getInstance() {
        return instance;
    }

    @Override
    public byte[] serialize(Message message) throws SerializationException {
        MutableMessage mutableMessage;
        if (message instanceof GenericMessage) {
            GenericMessage genericm = (GenericMessage) message;
            mutableMessage = new MutableMessage(genericm.getPayload(), genericm.getHeaders());
        } else {
            mutableMessage = (MutableMessage) message;
        }
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Schema<MutableMessage> schema = getSchema(MutableMessage.class);
            return ProtostuffIOUtil.toByteArray(mutableMessage, schema, buffer);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            buffer.clear();
        }
    }

    @Override
    public Message deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null) {
            return null;
        }
        try {
            MutableMessage obj = objenesis.newInstance(MutableMessage.class);
            Schema<MutableMessage> schema = getSchema(MutableMessage.class);
            ProtostuffIOUtil.mergeFrom(bytes, obj, schema);
            return obj;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private static Objenesis objenesis = new ObjenesisStd(true);

    private static final ProtoRedisSerializer instance = new ProtoRedisSerializer();

    private static Map<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<>();

    private static <T> Schema<T> getSchema(Class<T> cls) {
        Schema<T> schema = (Schema<T>) cachedSchema.get(cls);
        if (schema == null) {
            schema = RuntimeSchema.createFrom(cls);
            if (schema != null) {
                cachedSchema.put(cls, schema);
            }
        }
        return schema;
    }

}
