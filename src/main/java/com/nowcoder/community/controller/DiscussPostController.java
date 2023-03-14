package com.nowcoder.community.controller;


import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder holder;

    @Autowired
    private CommentService commentService;

    @Autowired
    private LikeService likeService;

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = holder.getUser();
        if (user == null) {
            return CommunityUtil.getJSONString(403, "你还没有登录哦");
        }
        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(user.getId());
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setCreateTime(new Date());

        discussPostService.addDiscussPost(discussPost);


        //报错的情况，后续统一处理
        return CommunityUtil.getJSONString(0,"发布成功");
    }

    //帖子的详情页
    @RequestMapping(path = "/detail/{discussPostId}", method = RequestMethod.GET)
    public String getDiscussDetail(Model model, @PathVariable("discussPostId") int discussPostId , Page page) {
        DiscussPost post = discussPostService.findDiscussPostById(discussPostId);

        model.addAttribute("post", post);
        //查询作者名字
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user", user);

        //查询当前登录的用户, 点赞应该使用当前登录的id，不能使用帖子作者的id
        User host = holder.getUser();

        //点赞数量
        long likeCount = likeService.findEntityLikeCount(CommunityConstant.ENTITY_TYPE_POST, discussPostId);
        model.addAttribute("likeCount", likeCount);

        //点赞状态
        if (host != null) {
            int status = likeService.findUserLikeStatusForEntity(host.getId(), CommunityConstant.ENTITY_TYPE_POST, discussPostId);
            model.addAttribute("likeStatus", status);
        }


//        System.out.println(post.getCommentCount());
        //处理帖子评论的逻辑
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(post.getCommentCount());

        //查询帖子下的评论
        List<Comment> commentsList = commentService.findComments(CommunityConstant.ENTITY_TYPE_POST,
                post.getId(), page.getOffset(), page.getLimit());
        //存储返回给model的数据
        List<Map<String, Object>> commentVOList = new ArrayList<>();

        if (commentsList != null) {
            for (Comment comment : commentsList) {
                //每个评论的vo
                Map<String, Object> commentVO = new HashMap<>();
                commentVO.put("comment", comment);
                commentVO.put("user", userService.findUserById(comment.getUserId()));
                //点赞
                likeCount = likeService.findEntityLikeCount(CommunityConstant.ENTITY_TYPE_COMMENT, comment.getId());
                commentVO.put("likeCount", likeCount);
                if (host != null) {
                    int status = likeService.findUserLikeStatusForEntity(host.getId(), CommunityConstant.ENTITY_TYPE_COMMENT, comment.getId());
                    commentVO.put("status", status);
                }

                //查询comment的回复,不进行分页
                List<Comment> reply = commentService.findComments(CommunityConstant.ENTITY_TYPE_COMMENT,
                        comment.getId(), 0, Integer.MAX_VALUE);
                //存储回复返回给model的数据
                List<Map<String, Object>> replyVOList = new ArrayList<>();
                for (Comment r: reply) {
                    Map<String, Object> replyVO = new HashMap<>();
                    replyVO.put("reply", r);
                    replyVO.put("user", userService.findUserById(r.getUserId()));
                    //回复的目标
                    User target = r.getTargetId() == 0 ? null : userService.findUserById(r.getTargetId());
                    replyVO.put("target", target); //target为0说明仅是对该评论的回复，并未回复某个具体的人

                    //回复的点赞
                    likeCount = likeService.findEntityLikeCount(CommunityConstant.ENTITY_TYPE_COMMENT, r.getId());
                    replyVO.put("likeCount", likeCount);
                    if (host != null) {
                        int status = likeService.findUserLikeStatusForEntity(host.getId(), CommunityConstant.ENTITY_TYPE_COMMENT, r.getId());
                        replyVO.put("status", status);
                    }

                    replyVOList.add(replyVO);
                }

                //将回复的数据放入vo中
                commentVO.put("replys",replyVOList);

                //回复量的查询
                int replyCount = commentService.findCommentRows(CommunityConstant.ENTITY_TYPE_COMMENT, comment.getId());
                commentVO.put("replyCount", replyCount);

                //所有数据装入list
                commentVOList.add(commentVO);
            }
        }

        //返回给model
        model.addAttribute("comments", commentVOList);

        return "/site/discuss-detail";

    }

}
