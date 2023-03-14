package com.nowcoder.community.controller;


import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController {

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder holder;

    @RequestMapping(path = "/like", method = RequestMethod.POST)
    @ResponseBody
    public String like(int entityType, int entityId){
        likeService.like(holder.getUser().getId(),entityType, entityId);

        //获取点赞的数量
        long entityLikeCount = likeService.findEntityLikeCount(entityType, entityId);

        //获取点赞的状态
        int status = likeService.findUserLikeStatusForEntity(holder.getUser().getId(), entityType, entityId);
        Map<String, Object> map = new HashMap<>();
        map.put("likeCount", entityLikeCount);
        map.put("likeStatus", status);
        return CommunityUtil.getJSONString(0, null, map);
    }

}
