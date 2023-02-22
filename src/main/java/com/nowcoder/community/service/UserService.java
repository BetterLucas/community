package com.nowcoder.community.service;

import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LoginTicketMapper ticketMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User findUserById(int id) {
        return userMapper.getUserById(id);
    }

    public User findUserByName (String name) {
        return userMapper.getUserByName(name);
    }
    //注册功能业务
    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();

        if (user == null) {
            throw new IllegalArgumentException("user参数不能为空！");
        }

        if (StringUtils.isBlank(user.getUserName())) {
            map.put("usernameMsg", "账号不能为空！");
            return map;
        }

        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空！");
            return map;
        }

        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空！");
            return map;
        }

        User u = userMapper.getUserByName(user.getUserName());
        if (u != null) {
            map.put("usernameMsg", "账号已存在！");
            return map;
        }

        u = userMapper.getUserByEmail(user.getEmail());
        if (u != null) {
            map.put("emailMsg", "邮箱已被注册！");
            return map;
        }
        //进行注册
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(1);//设置为1表示账号激活
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderURL(String.format("https://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        //在user表中添加用户信息
        userMapper.insertUser(user);

        //发送激活邮件
        Context context = new Context();
        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("email", user.getEmail());
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(),content,"账号激活");
        return map;
    }

    //激活功能
    public int activation(int userId, String code) {
        User user = userMapper.getUserById(userId);

        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        }
        else if (user.getActivationCode().equals(code)) {
            return ACTIVATION_SUCCESS;
        }
        else {
            return ACTIVATION_FAILURE;
        }
    }


    //登录功能业务
    public Map<String, Object> signOn(String username, String password , int expiredTime) {
        Map<String,Object> map = new HashMap<>();

        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "账号不能为空！");
            return map;
        }

        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空！");
            return map;
        }

        User u = userMapper.getUserByName(username);
        //账号校验
        if (u == null) {
            map.put("usernameMsg","账号不存在!");
            return map;
        }
        //状态校验 0为未激活，1为激活
        if (u.getStatus() == 0) {
            map.put("usernameMsg","账号未激活!");
            return map;
        }
        //密码校验
        String s = CommunityUtil.md5(password + u.getSalt());
        if (!u.getPassword().equals(s)) {
            map.put("passwordMsg", "密码不正确！");
            return map;
        }


        //成功时，生成登录凭证ticket
        String ticket = CommunityUtil.generateUUID();
        LoginTicket t = new LoginTicket();
        t.setStatus(0);
        t.setUserId(u.getId());
        t.setTicket(ticket);
        Date date = new Date(System.currentTimeMillis() + expiredTime);
        t.setExpired(date);
        int i = ticketMapper.insertTicket(t);
        if (i != 0) {
            map.put("ticket", t.getTicket());
        }

        return map;
    }
    //退出登录
    public void signOut(String ticket) {
        ticketMapper.updateStatus(ticket, -1);
    }
}
