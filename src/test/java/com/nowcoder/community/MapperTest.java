package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class MapperTest {
    @Autowired
    public DiscussPostMapper discussPostMapper;
    @Autowired
    public UserMapper userMapper;

    @Test
    public void testSelectPosts(){

        List<DiscussPost> discussPosts = discussPostMapper.getDiscussPosts(0,0,5);
        for (DiscussPost d : discussPosts) {
            System.out.println(d);
        }

    }
    @Test
    public void testGetRows() {
        System.out.println(discussPostMapper.getDiscussPostRows(0));
    }
    @Test
    public void testSelectUser(){
        User user = userMapper.getUserById(101);
        System.out.println(user);
    }
}
