package com.nowcoder.community;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;

import java.util.concurrent.TimeUnit;

@SpringBootTest
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void test1() {
        String rediskey = "test:count";
//        redisTemplate.opsForValue().set(rediskey,1);
        System.out.println(redisTemplate.opsForValue().get(rediskey));

    }

    @Test
    public void test2() {
        String rediskey = "test:user";
        redisTemplate.opsForHash().put(rediskey, "username", "lucas");
        System.out.println(redisTemplate.opsForHash().get(rediskey, "username"));
    }

    @Test
    public void test3() {
        String rediskey = "test:list";
        redisTemplate.opsForList().leftPush(rediskey,2);
        redisTemplate.opsForList().leftPush(rediskey,3);
        redisTemplate.opsForList().leftPush(rediskey,4);

        System.out.println(redisTemplate.opsForList().size(rediskey));
        System.out.println(redisTemplate.opsForList().index(rediskey,1));
        System.out.println(redisTemplate.opsForList().range(rediskey,0,2));

        System.out.println(redisTemplate.opsForList().leftPop(rediskey));
    }

    @Test
    public void test4() {
        String rediskey = "test:stu";
        redisTemplate.opsForZSet().add(rediskey, "张三", 70);
        redisTemplate.opsForZSet().add(rediskey, "李四", 60);
        redisTemplate.opsForZSet().add(rediskey, "王五", 90);
        redisTemplate.opsForZSet().add(rediskey, "赵六", 80);

        System.out.println(redisTemplate.opsForZSet().zCard(rediskey));
        System.out.println(redisTemplate.opsForZSet().score(rediskey, "王五"));
        System.out.println(redisTemplate.opsForZSet().rank(rediskey, "王五"));
        System.out.println(redisTemplate.opsForZSet().range(rediskey, 0,3));
    }


    @Test
    public void test5() {
        redisTemplate.delete("test:count");
        redisTemplate.expire("test:user", 10, TimeUnit.SECONDS);
    }

    /*redis的编程事务示例*/
    @Test

    public void test6() {
        Object obj = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                //开启事务
                operations.multi();
                redisTemplate.opsForSet().add("test:name", "阿达");
                redisTemplate.opsForSet().add("test:name", "函谷关");
                redisTemplate.opsForSet().add("test:name", "安德森");
                System.out.println(redisTemplate.opsForSet().members("test:name"));
                //提交事务
                return operations.exec();
            }
        });

        System.out.println(obj);

    }
}
