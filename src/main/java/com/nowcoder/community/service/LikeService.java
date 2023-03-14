package com.nowcoder.community.service;

import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    @Autowired
    private RedisTemplate redisTemplate;


    //点赞
    public void like(int userId, int entityType, int entityId) {
        String likeKey = RedisKeyUtil.getRedisLikeKey(entityType, entityId);
        Boolean isLike = redisTemplate.opsForSet().isMember(likeKey, userId);
        //已赞 取消
        if (isLike) {
            redisTemplate.opsForSet().remove(likeKey, userId);
        }
        else {
            redisTemplate.opsForSet().add(likeKey, userId);
        }
    }

    //获得某个实体的点赞数量
    public long findEntityLikeCount(int entityType, int entityId) {
        String likeKey = RedisKeyUtil.getRedisLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(likeKey);
    }

    //获得某人对某个实体的点赞状态（已赞，未赞，踩）  为拓展功能，返回int
    public int findUserLikeStatusForEntity(int userId, int entityType, int entityId) {
        String likeKey = RedisKeyUtil.getRedisLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().isMember(likeKey,userId) ? 1 : 0;
    }
}
