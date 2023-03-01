package com.nowcoder.community.dao;


import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {
    List<DiscussPost> getDiscussPosts(int userId, int offset, int limit);

    //@Param 用于给参数加别名
    //当使用动态sql， <if>时，只有一个参数必须加别名
    int getDiscussPostRows(@Param("userId") int userId);

    //发布帖子
    int insertDiscussPost(DiscussPost discussPost);

    //查询帖子详情
    DiscussPost getDiscussPostById(int id);
}
