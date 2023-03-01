package com.nowcoder.community.dao;


import com.nowcoder.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {
    //查询评论的总条数,entityType表示查询该类型的评论
    int getCommentsRows(int entityType, int entityId);

    //查询所有评论，并分页
    List<Comment> getComments(int entityType, int entityId, int offset, int limit);
}
