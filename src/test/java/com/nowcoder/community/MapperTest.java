package com.nowcoder.community;

import com.nowcoder.community.dao.*;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.Message;
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
    private CommentMapper commentMapper;

    @Autowired
    public LoginTicketMapper loginTicketMapper;


    @Autowired
    public MessageMapper messageMapper;

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


    @Test
    public void testMessageMapper() {
        List<Message> messages = messageMapper.selectConversations(111, 0, 5);
        for (Message m : messages) {
            System.out.println(m);
        }


        int count = messageMapper.selectConversationsCount(111);
        System.out.println(count);

        messages =  messageMapper.selectLetters("111_112",0,10);
        for (Message m : messages) {
            System.out.println(m);
        }

        count = messageMapper.selectLetterCount("111_112");
        System.out.println(count);

        count = messageMapper.selectLetterUnreadCount(131,"111_131");
        System.out.println(count);

    }
}
