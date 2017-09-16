package com.pengjinfei.maven.configuration.cache;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.util.List;

/**
 * Created on 8/30/17
 *
 * @author Pengjinfei
 */
@Configuration
public class RedisScriptConfiguration{

    @Bean
    public RedisScript<List> script() {
        DefaultRedisScript<List> script = new DefaultRedisScript<>();
        script.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/priorityQueue.lua")));
        script.setResultType(List.class);
        return script;
    }

    @Bean
    public RedisScript<Integer> list2ZseScript() {
        DefaultRedisScript<Integer> script = new DefaultRedisScript<>();
        script.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/listToZset.lua")));
        script.setResultType(Integer.class);
        return script;
    }

    @Bean
    public RedisScript<List> updateAndDel() {
        DefaultRedisScript<List> script = new DefaultRedisScript<>();
        script.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/addAndDel.lua")));
        script.setResultType(List.class);
        return script;
    }
}

