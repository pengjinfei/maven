package com.pengjinfei.maven.configuration.cache;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created on 8/30/17
 *
 * @author Pengjinfei
 */
@Component
public class PriorityQueueRedisScript extends DefaultRedisScript<List> {

    public PriorityQueueRedisScript() {
        setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/priorityQueue.lua")));
        setResultType(List.class);
    }
}
