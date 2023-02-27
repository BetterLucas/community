package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;

import com.nowcoder.community.util.CommunityUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

@SpringBootTest
public class MapperTest {
    @Autowired
    public DiscussPostMapper discussPostMapper;
    @Autowired
    public UserMapper userMapper;

    @Autowired
    public LoginTicketMapper loginTicketMapper;
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


    @Test
    public void testTicketMapper() {
//        LoginTicket ticket = loginTicketMapper.getTicket("");
//        System.out.println(ticket);
//        LoginTicket t = new LoginTicket();
//        t.setUserId(2);
//        t.setTicket(CommunityUtil.generateUUID());
//
//        t.setStatus(1);
//        System.out.println(new Date());
//        t.setExpired(new Date());
//        int i = loginTicketMapper.insertTicket(t);
//        System.out.println(i);

        int i = loginTicketMapper.updateStatus("c964a8d7142d4195bad37afdf9c5c7b0", 0);
        System.out.println(i);


    }
    @Test
    public void testMD5() {
        String s = CommunityUtil.md5("456024e8");
        System.out.println(s);
    }
}
