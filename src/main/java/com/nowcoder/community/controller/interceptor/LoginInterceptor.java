package com.nowcoder.community.controller.interceptor;

import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;


@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private LoginTicketMapper loginTicketMapper;
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private HostHolder hostHolder;
    //在controller之前持有数据
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //从cookie中取ticket凭证
        String ticket = CommunityUtil.getValue(request, "ticket");

        if (ticket != null) {
            //校验凭证是否有效,查询对应的userid
            LoginTicket t = loginTicketMapper.getTicket(ticket);
            //检查t是否为空，是否已经登录，是否有效
            if (t != null && t.getExpired().after(new Date()) && t.getStatus() == 0) {
                //若有效，查询userid
                int userId = t.getUserId();
                User user = userMapper.getUserById(userId);
                //在本次请求中持有user
                hostHolder.setUser(user);
            }

        }
        return true;
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //从hostHolder中持有user
        User user = hostHolder.getUser();

        if (user != null && modelAndView != null) {
            //将其放入模板中，用于渲染
            modelAndView.addObject("loginUser", user);
        }
    }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //不再持有user
        hostHolder.clear();
    }
}
