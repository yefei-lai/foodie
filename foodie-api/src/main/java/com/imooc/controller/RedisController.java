package com.imooc.controller;

import common.imooc.utils.RedisOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ApiIgnore
@RestController
@RequestMapping("redis")
public class RedisController {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisOperator redisOperator;

    @GetMapping("/set")
    public Object set(String key, String value){

        redisTemplate.opsForValue().set(key, value);

        return "OK";
    }

    @GetMapping("/get")
    public String get(String key){

        return (String) redisTemplate.opsForValue().get(key);
    }

    @GetMapping("/delete")
    public Object delete(String key){

        redisTemplate.delete(key);

        return "OK";
    }

    /**
     * 批量查询mget
     * @param keys
     * @return
     */
    @GetMapping("/mget")
    public Object mget(String... keys){

        List<String> keysList = Arrays.asList(keys);

        return redisOperator.mget(keysList);
    }

}
