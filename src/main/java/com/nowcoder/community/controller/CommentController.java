package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    CommentService commentService;

    @Autowired
    HostHolder hostHolder;

    //添加评论
    @RequestMapping(path = "/add/{discussPostId}", method = RequestMethod.POST)
    public String addComments(@PathVariable("discussPostId") int discussPostId, Comment comment) {
        //获取当前评论的用户
        User user = hostHolder.getUser();
        //剩余的字段由前端传入
        comment.setUserId(user.getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());

        commentService.addComments(comment);

        return "redirect:/discuss/detail/" + discussPostId;
    }

}
