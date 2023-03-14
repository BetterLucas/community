package com.nowcoder.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        //设置key的序列化方式
        template.setKeySerializer(RedisSerializer.string());

        //设置一般value的序列化方式
        template.setKeySerializer(RedisSerializer.json());

        //设置hash数据的key序列化方式
        template.setKeySerializer(RedisSerializer.string());
        //设置hash数据的value序列化方式
        template.setKeySerializer(RedisSerializer.json());

        template.afterPropertiesSet();

        return template;
    }
}
