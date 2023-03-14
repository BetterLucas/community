package com.nowcoder.community.service;

import com.nowcoder.community.dao.CommentMapper;
import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    //注入敏感词过滤
    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private DiscussPostService discussPostService;

    public int findCommentRows(int entityType, int entityId) {
        return commentMapper.getCommentsRows(entityType,entityId);
    }

    public List<Comment> findComments(int entityType, int entityId, int offset, int limit) {
        return commentMapper.getComments(entityType, entityId, offset,limit);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int addComments(Comment comment) {
        if(comment == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }

        //过滤标签和敏感词
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));

        //添加帖子的评论
        int rows = commentMapper.insertComment(comment);

        //更新帖子评论的数量
        if (comment.getEntityType() == CommunityConstant.ENTITY_TYPE_POST) {
            //获取当前帖子下的评论数量
            int commentsRows = commentMapper.getCommentsRows(comment.getEntityType(), comment.getEntityId());
            //根据帖子的id更新其行数，所以传入的是当前实体（帖子）的id
            discussPostService.updateCommentCount(comment.getEntityId(), commentsRows);
        }

        return rows;
    }
}
