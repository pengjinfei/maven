package com.pengjinfei.maven.configuration;

import org.nustaq.serialization.FSTConfiguration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

/**
 * Created on 7/22/17
 *
 * @author Pengjinfei
 */
public class FstSerializer implements RedisSerializer<Object> {

    private static FSTConfiguration configuration = FSTConfiguration
            .createDefaultConfiguration();

    @Override
    public byte[] serialize(Object o) throws SerializationException {
        if (o == null) {
            return null;
        }
        return configuration.asByteArray(o);
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null) {
            return null;
        }
        return configuration.asObject(bytes);
    }
}
