package com.pengjinfei.maven.controller;

import com.pengjinfei.maven.entity.Person;
import com.pengjinfei.maven.service.cache.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.web.bind.annotation.*;
import redis.clients.jedis.Protocol;

import java.util.Arrays;
import java.util.List;

/**
 * Created on 8/30/17
 *
 * @author Pengjinfei
 */
@RequestMapping("/redis")
@RestController
public class RedisRest {

    @Autowired
    private RedisTemplate template;

    @Autowired
    RedisScript<List> script;

    @Autowired
    RedisScript<Integer> list2ZseScript;

    @Autowired
    @Qualifier("cacheServie")
    private CacheService cacheService;

    @Autowired
    RedisScript<List> updateAndDel;

    @PostMapping("/{key}")
    public void addList(@PathVariable("key")String key, @RequestBody List<Person> people) {
        for (Person person : people) {
            template.opsForList().leftPush(key, person);
        }
    }

    @PostMapping("/{pre}/{num}")
    public List<Person> getList(@PathVariable("pre") String pre, @PathVariable("num") String num) {
        Object execute = template.execute(script, Arrays.asList(new Object[]{pre + ":", num}));
        return (List<Person>) execute;
    }

    @GetMapping("/transfer/{lkey}/{zkey}/{score}")
    public void list2Zset(@PathVariable("lkey") String lkey,@PathVariable("zkey") String zkey,
                          @PathVariable("score") String score) {
        Object execute = template.execute(list2ZseScript, Arrays.asList(new Object[]{lkey, zkey, score}));
        System.out.println(execute);
    }

    @GetMapping("/cache")
    public Person getPerson() {
        return cacheService.getPerson();
    }

    @PostMapping("/del")
    public void del(@RequestParam("keys") String keys,@RequestParam("args") String args) {
        Object execute = template.execute(updateAndDel, Arrays.asList(keys.split(",")), args.split(","));
        System.out.println("good");
    }

    @PostMapping("/hset/{key}/{field}/{value}")
    public void hset(@PathVariable("key")String key,
                     @PathVariable("field")String field,
                     @PathVariable("value")long value) {
        RedisSerializer keySerializer = template.getKeySerializer();
        RedisSerializer valueSerializer = template.getValueSerializer();
        template.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                redisConnection.hSet(keySerializer.serialize(key), valueSerializer.serialize(field), Protocol.toByteArray(value));
                return null;
            }
        });
    }

    private  byte[] getBytes(long data)
    {
        byte[] bytes = new byte[8];
        bytes[0] = (byte) (data & 0xff);
        bytes[1] = (byte) ((data >> 8) & 0xff);
        bytes[2] = (byte) ((data >> 16) & 0xff);
        bytes[3] = (byte) ((data >> 24) & 0xff);
        bytes[4] = (byte) ((data >> 32) & 0xff);
        bytes[5] = (byte) ((data >> 40) & 0xff);
        bytes[6] = (byte) ((data >> 48) & 0xff);
        bytes[7] = (byte) ((data >> 56) & 0xff);
        return bytes;
    }
}
