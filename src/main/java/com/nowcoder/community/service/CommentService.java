package com.nowcoder.community.service;

import com.nowcoder.community.dao.CommentMapper;
import com.nowcoder.community.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    public int findCommentRows(int entityType, int entityId) {
        return commentMapper.getCommentsRows(entityType,entityId);
    }

    public List<Comment> findComments(int entityType, int entityId, int offset, int limit) {
        return commentMapper.getComments(entityType, entityId, offset,limit);
    }
}