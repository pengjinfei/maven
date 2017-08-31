package com.pengjinfei.maven.controller;

import com.pengjinfei.maven.entity.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.web.bind.annotation.*;

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
}
